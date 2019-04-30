#!/usr/bin/env groovy

def call(String buildResult, String threadId = null) {
  def slackResponse = null

  if ( buildResult == "STARTED") {
    slackResponse = slackSend color: "good", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} has started", channel: threadId
  } else if ( buildResult == "SUCCESS" ) {
    slackResponse = slackSend color: "good", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was successful", channel: threadId
  } else if( buildResult == "FAILURE" ) {
    slackResponse = slackSend color: "danger", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was failed", channel: threadId
  } else if( buildResult == "UNSTABLE" ) {
    slackResponse = slackSend color: "warning", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was unstable", channel: threadId
  } else {
    slackResponse = slackSend color: "danger", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} its resulat was unclear", channel: threadId
  }
  slackResponse
}
