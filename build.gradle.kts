plugins {
  java
}

val assignmentId: String by extra("H02")
val firstName: String by extra("_not_set_")
val lastName: String by extra("_not_set_")

tasks {
  create<Jar>("prepareSubmission") {
    doFirst {
      if (firstName == "_not_set_" || lastName == "_not_set_") {
        throw GradleException("firstName or lastName not set!")
      }
    }
    // include source files in output jar
    from(sourceSets.main.get().allSource)
    // replace placeholders in resource directory
    // e.g. the submission-info.json file
    expand(project.properties)
    // set the name of the output jar
    archiveFileName.set("$assignmentId-$lastName-$firstName-submission.jar")
  }
}
