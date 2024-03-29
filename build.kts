package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.*
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.*
import jetbrains.buildServer.configs.kotlin.v2019_2.templates.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.*

object Build : BuildType({
    name = "Build"
    vcs {
        root(HttpsGithubComMnementh64multiThreadingPuzzleGitRefsHeadsMaster) // Define your VCS root here
    }
    steps {
        maven {
            goals = "clean package" // Maven goals to execute
            jdkHome = "%env.JDK_1_8%" // Path to Java 1.8 installation
            jvmArgs = "-Xmx1024m" // JVM arguments
            runnerArgs = "-Dmaven.test.failure.ignore=true" // Additional runner arguments
        }

        script {
            name = "Create Fat JAR"
            scriptContent = """
                    mvn -f ${'$'}teamcity.build.checkoutDir/pom.xml package
                    cd ${'$'}teamcity.build.checkoutDir/target
                    jar -cvf MyFatJar.jar *
                    echo "##teamcity[publishArtifacts '${'$'}teamcity.build.checkoutDir/target/MyFatJar.jar => MyFatJar.jar']"
                """.trimIndent()
        }

        script {
            name = "List target content"
            scriptContent = """
                    ls -ilas ${'$'}teamcity.build.checkoutDir/target
                """.trimIndent()
        }
    }
})
