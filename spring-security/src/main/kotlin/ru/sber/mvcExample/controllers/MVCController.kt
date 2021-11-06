package ru.sber.mvcExample.controllers

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.sber.mvcExample.repository.AddressBookRepository
import ru.sber.mvcExample.repository.AddressInfo
import ru.sber.mvcExample.repository.AddressSearchForm
import java.time.Clock

@Controller
class MVCController {
    lateinit var addressBookRepository: AddressBookRepository
    @Autowired set

    lateinit var clock: Clock
    @Autowired set

    @PostMapping("/app/add")
    fun add(@ModelAttribute element: AddressInfo): String {
        println("---------->$element")
        val id: String? = addressBookRepository.add(element)
        if (id == null) {
            logger.error("Add went wrong. Element $element wasn't added")
        } else {
            logger.info("Element with $id added")
        }
        return "redirect:/app/list"
    }

    @GetMapping("/app/list")
    fun list(@ModelAttribute searchForm: AddressSearchForm, model: Model): String {
        val searchTemplate = AddressInfo(searchForm.name ?: "", searchForm.address ?: "")
        println("--------------->SearchForm = $searchTemplate")
        val foundEntries = addressBookRepository.list(searchTemplate)
        model.addAttribute("list_of_elements", foundEntries.toList())
        logger.info("Listed ${foundEntries.size} elements")
        return "app/list"
    }

    @GetMapping("/app/{id}/view")
    fun viewById(@PathVariable("id") id: String, model: Model): String {
        val element = addressBookRepository.view(id)
        if (element == null) {
            logger.error("No element with id = $id")
        } else {
            logger.info("Viewing element with id = $id")
        }
        println("------------------------>$element")
        model.addAttribute("element", element)
        return "app/viewById"
    }

    @PostMapping("/app/{id}/edit")
    fun editById(@PathVariable("id") id: String, @ModelAttribute searchForm: AddressSearchForm, model: Model): String {
        val element = addressBookRepository.view(id)
        if (element == null) {
            logger.error("No element with id = $id, can't modify")
        } else {
            val editedElement = addressBookRepository.edit(id, searchForm)
            if (editedElement != null) {
                logger.info("Element $id edited all right: $editedElement")
            } else {
                logger.error("Editing error!")
            }
        }

        return "redirect:/app/list"
    }

    @GetMapping("/app/{id}/edit/form")
    fun editForm(@PathVariable("id") id: String, model: Model): String {
        model.addAttribute("id", id)
        return "/app/editById"
    }

    @GetMapping("/app/{id}/delete")
    fun deleteById(@PathVariable("id") id: String): String {
        if (addressBookRepository.delete(id)) {
            logger.info("Element $id was deleted")
        } else {
            logger.error("Error during deletion $id !")
        }
        return "redirect:/app/list"
    }

    companion object {
        var logger: Log = LogFactory.getLog(this::class.java)
    }
}