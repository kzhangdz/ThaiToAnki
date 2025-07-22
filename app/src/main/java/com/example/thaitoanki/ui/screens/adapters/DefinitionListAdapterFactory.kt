package com.example.thaitoanki.ui.screens.adapters

import com.example.thaitoanki.data.network.Definition
import com.example.thaitoanki.data.network.DefinitionType

interface DefinitionListAdapterFactory {
    companion object {
        fun createFromDefinitionType(definitionType: DefinitionType, definitions: List<Definition>, definitionBlockOnClick: (Int) -> Unit): DefinitionListAdapter {
            return when (definitionType) {
                DefinitionType.DEFINITION -> DefinitionListAdapter(definitions, definitionBlockOnClick)
                DefinitionType.EXAMPLE -> ExampleListAdapter(definitions, definitionBlockOnClick)
                DefinitionType.SENTENCE -> SentenceListAdapter(definitions, definitionBlockOnClick)
            }
        }
    }
}