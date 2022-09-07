package io.jenkins.plugins.back_merge

import spock.lang.Specification

class HelloWorldTest extends Specification {

    def 'hello world'() {
        expect:
        new HelloWorld().sayHello() == 'Hello world'
    }
}
