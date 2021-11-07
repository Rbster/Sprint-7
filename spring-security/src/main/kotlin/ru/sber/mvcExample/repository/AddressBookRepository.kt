package ru.sber.mvcExample.repository

import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class AddressBookRepository {
    private val repoRealisation = ConcurrentHashMap<String, AddressInfo>()
    private fun nextId(): String {
        if (repoRealisation.size == 0) {
            return "id1"
        } else {
            val idxes = repoRealisation.keys().asSequence()
                .map { it.slice(2 until it.length).toInt() }
                .sorted()
                .toList()
            if (idxes[idxes.size - 1] == idxes.size) {
                return "id${idxes.size + 1}"
            }
            for (i in 1..idxes[idxes.size - 1]) {
                if (i != idxes[i - 1]) {
                    return "id$i"
                }
            }
            return "id0" // never happens
        }
    }
    fun clearRepository() {
        repoRealisation.clear()
    }
    fun add(element: AddressInfo): String? {
        val id = nextId()
        return try {
            repoRealisation[id] = element
            id
        } catch (e: Exception) {
            null
        }
    }
    @PostAuthorize("hasRole('ADMIN')")
    fun delete(id: String): Boolean {
        return  try { repoRealisation.remove(id) != null } catch (e: Exception) { false }
    }
    fun edit(id: String, form: AddressSearchForm): AddressInfo?  {
        val oldElement = repoRealisation[id] ?: return null
        if (form.name == null && form.address == null) {
            return oldElement
        }
        val newElement = AddressInfo(form.name ?: oldElement.name, form.address ?: oldElement.address)
        return  try { repoRealisation.replace(id, newElement) } catch (e: Exception) { null }
    }
    fun view(id: String) = repoRealisation[id]
    fun list(searchTemplate: AddressInfo = AddressInfo("", "")) = repoRealisation
        .toList()
        .asSequence()
        .filter { if (searchTemplate.name != "") searchTemplate.name == it.second.name else true }
        .filter { if (searchTemplate.address != "") searchTemplate.address == it.second.address else true }
        .toMap()
}

data class AddressInfo(var name: String, var address: String)
data class AddressSearchForm(val name: String?, val address: String?)
data class LoginFormModel(val log: String, val password: String)
