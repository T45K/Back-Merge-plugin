package io.jenkins.plugins.back_merge


import hudson.model.Label
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class BackMergeBuilderTest extends Specification {

    @Rule
    JenkinsRule jenkins = new JenkinsRule()

    final def name = "Bobby"

    def 'test config round-trip'() {
        when:
        def project = jenkins.createFreeStyleProject()
        project.buildersList << new BackMergeBuilder(name)
        project = jenkins.configRoundtrip(project)

        then:
        jenkins.assertEqualDataBoundBeans(new BackMergeBuilder(name), project.buildersList[0])
    }

    def 'test config round-trip French'() {
        when:
        def project = jenkins.createFreeStyleProject()
        final def builder = new BackMergeBuilder(name)
        builder.useFrench = true
        project.buildersList << builder
        project = jenkins.configRoundtrip(project)

        final def lhs = new BackMergeBuilder(name)
        lhs.useFrench = true

        then:
        jenkins.assertEqualDataBoundBeans(lhs, project.buildersList[0])
    }

    def 'test build'() {
        when:
        final def project = jenkins.createFreeStyleProject()
        final def builder = new BackMergeBuilder(name)
        project.buildersList.add(builder)

        then:
        final def build = jenkins.buildAndAssertSuccess(project)
        jenkins.assertLogContains("Hello, $name", build)
    }

    def 'test build French'() {
        when:
        final def project = jenkins.createFreeStyleProject()
        final def builder = new BackMergeBuilder(name)
        builder.useFrench = true
        project.buildersList.add(builder)

        then:
        final def build = jenkins.buildAndAssertSuccess(project)
        jenkins.assertLogContains("Bonjour, $name", build)
    }

    def 'test scripted pipeline'() {
        when:
        final def agentLabel = "my-agent"
        jenkins.createOnlineSlave(Label.get(agentLabel))
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-scripted-pipeline")
        final def pipelineScript = """\
node {
  greet '$name'
}"""

        job.definition = new CpsFlowDefinition(pipelineScript, true)

        then:
        final def completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0))
        final def expectedString = "Hello, $name!"
        jenkins.assertLogContains(expectedString, completedBuild)
    }

}
