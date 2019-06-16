#!/usr/bin/env groovy

def call(String environmentSufix, String environment, boolean pushToRegistry, String tagName, String frontEndHost) {
  withCredentials([file(credentialsId: "${environmentSufix}_firebase_service_account_key", variable: 'serviceAccountKey_json')]) {
               sh "cp $serviceAccountKey_json src/main/resources/serviceAccountKey.json"
               sh "mvn clean install -Dmaven.test.skip=true -Dapp.build.buildNumber=${env.BUILD_NUMBER} -Dapp.environment=${environment} -Dapp.commit=${env.GIT_COMMIT}"
  }

  withCredentials([string(credentialsId: "${environmentSufix}_aws_access", variable: 'aws_access'),
                  string(credentialsId: "${environmentSufix}_aws_secret", variable: 'aws_secret'),
                  string(credentialsId: "mailgun_access_key", variable: 'mailgun_key'),
                  string(credentialsId: "${environmentSufix}_dynamodb_table_prefix", variable: 'dynamodb_table_prefix'),
                  string(credentialsId: "${environmentSufix}_s3_bucket", variable: 's3_bucket'),
                  string(credentialsId: "${environmentSufix}_aws_thumbnail_host", variable: 'aws_thumbnail_host'),
                  string(credentialsId: "${environmentSufix}_transcoder_pipelineId", variable: 'transcoder_pipelineId'),
                  string(credentialsId: "${environmentSufix}_db_host", variable: 'db_host'),
                  string(credentialsId: "${environmentSufix}_db_user", variable: 'db_user'),
                  string(credentialsId: "${environmentSufix}_db_password", variable: 'db_password'),
                  string(credentialsId: "${environmentSufix}_app_insights_key", variable: 'app_insights_key'),
                  string(credentialsId: "mailgun_url", variable: 'mailgun_url'),
                  string(credentialsId: "mailchimp_api_key", variable: 'mailchimp_api_key'),
                  string(credentialsId: "mailchimp_list_id", variable: 'mailchimp_list_id'),
                  string(credentialsId: "${environmentSufix}_firebase_database_url", variable: 'firebase_database_url'),
                  string(credentialsId: "${environmentSufix}_jwt_key", variable: 'jwt_key')
                  ]) {
     script {
       send_grid_key = getStringCredential("${environmentSufix}_send_grid_key", true)
       app = docker.build ("scholarjet.azurecr.io/scholarjet-webapp", '''--build-arg aws_access=$aws_access  \
             --build-arg aws_default_bucket=$s3_bucket \
             --build-arg aws_thumbnail_host=$aws_thumbnail_host \
             --build-arg aws_secret=$aws_secret  \
             --build-arg aws_transcoder_pipeline=$transcoder_pipelineId \
             --build-arg dynamodb_table_prefix=$dynamodb_table_prefix \
             --build-arg mailgun_access_key=$mailgun_key \
             --build-arg mailgun_url=$mailgun_url \
             --build-arg mailchimpApiKey=$mailchimp_api_key \
             --build-arg mailchimpListId=$mailchimp_list_id \
             --build-arg postToMailChimp="true" \
             --build-arg front_end_host=$frontEndHost \
             --build-arg db_user=$db_user \
             --build-arg db_host=$db_host \
             --build-arg db_password=$db_password \
             --build-arg environment=$ENV \
             --build-arg firebase_database_url=$firebase_database_url \
             --build-arg APPLICATION_INSIGHTS_IKEY=$app_insights_key \
             --build-arg jwt_key=$jwt_key \
             --build-arg sendGridKey=$send_grid_key \
             .''')
         if (pushToRegistry) {
           docker.withRegistry('https://scholarjet.azurecr.io', 'azure_acr_jenkins_principal') {
                 app.push(tagName)
           }
         }
     }
  }
}

def String getStringCredential(String credentialKey, Boolean optional) {
  try {
    withCredentials([string(credentialsId: credentialKey, variable: 'credentialValue')]) {
     $credentialValue
   }
  } catch (Exception ex) {
    if (!optional) {
      throw ex
    }
    null
  }
}
