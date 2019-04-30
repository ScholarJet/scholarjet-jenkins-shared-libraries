#!/usr/bin/env groovy

def call(String buildResult, String threadId) {
  def threadId = null

  if ( buildResult == "STARTED") {
    threadId = slackSend color: "good", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} has started", channel: threadId
  } else if ( buildResult == "SUCCESS" ) {
    threadId = slackSend color: "good", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was successful", channel: threadId
  } else if( buildResult == "FAILURE" ) {
    threadId = slackSend color: "danger", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was failed", channel: threadId
  } else if( buildResult == "UNSTABLE" ) {
    threadId = slackSend color: "warning", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was unstable", channel: threadId
  } else {
    threadId = slackSend color: "danger", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} its resulat was unclear", channel: threadId
  }
  threadId
}
