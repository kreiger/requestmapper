package com.viartemev.requestmapper.model

data class Path(private val pathElements: List<PathElement>) {
    constructor(string: String) : this(string.split(Regex("""(?=\{)|(?<=\})""")).map { PathElement(it) })

    fun addPathVariablesTypes(parametersNameWithType: Map<String, String>): Path {
        return Path(pathElements.flatMap {
            it.flatMap {
                val names = it.substringBefore(':').split(',')
                names.map {
                    val type = parametersNameWithType.getOrDefault(it, "Object").ifBlank { "String" }
                    "$type:$it"
                }
            }
        })
    }

    fun toFullPath() = pathElements.joinToString("") { it.value }

    companion object {

        fun isSubpathOf(sourcePath: Path, targetPath: Path): Boolean {
            val sourcePathElements = sourcePath.pathElements.drop(1)
            val targetPathElements = targetPath.pathElements.drop(1)
            val allSourceElementsArePathVariables = sourcePathElements.all { it.isPathVariable }

            return containsAll(sourcePathElements, targetPathElements, allSourceElementsArePathVariables)
        }

        private tailrec fun containsAll(sourcePathElements: List<PathElement>, targetPathElements: List<PathElement>, allSourceElementsArePathVariables: Boolean): Boolean {
            if (sourcePathElements.size < targetPathElements.size) {
                return false
            }

            val hasExactMatching = sourcePathElements.subList(0, targetPathElements.size).any { !it.isPathVariable }
            val pathElementsAreEqual = sourcePathElements
                .zip(targetPathElements)
                .all { (popupElement, searchPattern) ->
                    popupElement.compareToSearchPattern(searchPattern) || popupElement.value.startsWith(searchPattern.value)
                }

            if (pathElementsAreEqual && (hasExactMatching || allSourceElementsArePathVariables)) {
                return true
            }

            return containsAll(sourcePathElements.drop(1), targetPathElements, allSourceElementsArePathVariables)
        }
    }
}
