package ru.sber.mvcExample.controllers

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.sber.mvcExample.repository.AddressBookRepository
import ru.sber.mvcExample.repository.AddressInfo
import ru.sber.mvcExample.repository.AddressSearchForm
import java.time.Clock

@RestController
@RequestMapping("/api")
class RestController {
    lateinit var addressBookRepository: AddressBookRepository
        @Autowired set

    lateinit var clock: Clock
        @Autowired set

    @PostMapping("/app/add", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun add(@RequestBody element: AddressInfo): ResponseEntity<Pair<String, AddressInfo>> {
        println("---------->$element")
        val id: String? = addressBookRepository.add(element)
        return if (id == null) {
            logger.error("Add went wrong. Element $element wasn't added")
            ResponseEntity.internalServerError().build()
        } else {
            logger.info("Element with $id added")
            ResponseEntity.ok(id to element)
        }
    }

    @GetMapping("/app/list", produces = [APPLICATION_JSON_VALUE])
    fun list(@ModelAttribute searchForm: AddressSearchForm, model: Model): ResponseEntity<Map<String, AddressInfo>> {
        val searchTemplate = AddressInfo(searchForm.name ?: "", searchForm.address ?: "")
        println("--------------->SearchForm = $searchTemplate")
        val foundEntries = addressBookRepository.list(searchTemplate)
        model.addAttribute("list_of_elements", foundEntries.toList())
        logger.info("Listed ${foundEntries.size} elements")
        return ResponseEntity.ok(foundEntries)
    }

    @GetMapping("/app/{id}/view", produces = [APPLICATION_JSON_VALUE])
    fun viewById(@PathVariable("id") id: String, model: Model): ResponseEntity<Pair<String, AddressInfo>> {
        val element = addressBookRepository.view(id)
        if (element == null) {
            logger.error("No element with id = $id")
        } else {
            logger.info("Viewing element with id = $id")
        }
        println("------------------------>$element")
        model.addAttribute("element", element)
        return if (element != null) {
            ResponseEntity.ok(id to element)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/app/{id}/edit", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun editById(@PathVariable("id") id: String,
                 @RequestBody searchForm: AddressSearchForm,
                 model: Model): ResponseEntity<Pair<String, AddressInfo>> {
        val element = addressBookRepository.view(id)
        return if (element == null) {
            logger.error("No element with id = $id, can't modify")
            ResponseEntity.notFound().build()
        } else {
            val editedElement = addressBookRepository.edit(id, searchForm)
            if (editedElement != null) {
                logger.info("Element $id edited all right: $editedElement")
                ResponseEntity.ok(id to editedElement)
            } else {
                logger.error("Editing error!")
                ResponseEntity.internalServerError().build()

            }
        }
    }

    @DeleteMapping("/app/{id}/delete", produces = [APPLICATION_JSON_VALUE])
    fun deleteById(@PathVariable("id") id: String): ResponseEntity<Any> {
        if (addressBookRepository.delete(id)) {
            logger.info("Element $id was deleted")
        } else {
            logger.error("Error during deletion $id ! or there was no element with this id")
        }
        return ResponseEntity.noContent().build()
    }

    companion object {
        var logger: Log = LogFactory.getLog(this::class.java)
    }
}