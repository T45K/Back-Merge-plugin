package io.jenkins.plugins.back_merge

import java.io.PrintStream

object JenkinsLogger {
    lateinit var delegate: PrintStream

    fun info(message: String) {
        delegate.println(message)
    }
}
