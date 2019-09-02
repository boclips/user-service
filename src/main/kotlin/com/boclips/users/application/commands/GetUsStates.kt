package com.boclips.users.application.commands

import com.boclips.users.domain.model.school.State
import org.springframework.stereotype.Service
import java.util.TreeMap

@Service
class GetUsStates {
    operator fun invoke(): List<State> {
        return states().map { State(id = it.key, name = it.value) }
    }

    companion object {
        private fun states(): Map<String, String> {
            val states = TreeMap<String, String>()
            states.put("AL", "Alabama")
            states.put("AK", "Alaska")
            states.put("AB", "Alberta")
            states.put("AZ", "Arizona")
            states.put("AR", "Arkansas")
            states.put("BC", "British Columbia")
            states.put("CA", "California")
            states.put("CO", "Colorado")
            states.put("CT", "Connecticut")
            states.put("DE", "Delaware")
            states.put("DC", "District Of Columbia")
            states.put("FL", "Florida")
            states.put("GA", "Georgia")
            states.put("GU", "Guam")
            states.put("HI", "Hawaii")
            states.put("ID", "Idaho")
            states.put("IL", "Illinois")
            states.put("IN", "Indiana")
            states.put("IA", "Iowa")
            states.put("KS", "Kansas")
            states.put("KY", "Kentucky")
            states.put("LA", "Louisiana")
            states.put("ME", "Maine")
            states.put("MB", "Manitoba")
            states.put("MD", "Maryland")
            states.put("MA", "Massachusetts")
            states.put("MI", "Michigan")
            states.put("MN", "Minnesota")
            states.put("MS", "Mississippi")
            states.put("MO", "Missouri")
            states.put("MT", "Montana")
            states.put("NE", "Nebraska")
            states.put("NV", "Nevada")
            states.put("NB", "New Brunswick")
            states.put("NH", "New Hampshire")
            states.put("NJ", "New Jersey")
            states.put("NM", "New Mexico")
            states.put("NY", "New York")
            states.put("NF", "Newfoundland")
            states.put("NC", "North Carolina")
            states.put("ND", "North Dakota")
            states.put("NT", "Northwest Territories")
            states.put("NS", "Nova Scotia")
            states.put("NU", "Nunavut")
            states.put("OH", "Ohio")
            states.put("OK", "Oklahoma")
            states.put("ON", "Ontario")
            states.put("OR", "Oregon")
            states.put("PA", "Pennsylvania")
            states.put("PE", "Prince Edward Island")
            states.put("PR", "Puerto Rico")
            states.put("QC", "Quebec")
            states.put("RI", "Rhode Island")
            states.put("SK", "Saskatchewan")
            states.put("SC", "South Carolina")
            states.put("SD", "South Dakota")
            states.put("TN", "Tennessee")
            states.put("TX", "Texas")
            states.put("UT", "Utah")
            states.put("VT", "Vermont")
            states.put("VI", "Virgin Islands")
            states.put("VA", "Virginia")
            states.put("WA", "Washington")
            states.put("WV", "West Virginia")
            states.put("WI", "Wisconsin")
            states.put("WY", "Wyoming")
            states.put("YT", "Yukon Territory")

            return states
        }
    }
}