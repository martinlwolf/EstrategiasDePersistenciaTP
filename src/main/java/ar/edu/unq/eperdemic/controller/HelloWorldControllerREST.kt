package ar.edu.unq.eperdemic.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/hello")
class HelloWorldControllerRest() {

    @GetMapping
    fun helloWorld() = "Hello world!"
}