package com.viartemev.requestmapper.annotations.spring

import com.intellij.psi.PsiAnnotation

class GetMapping(psiAnnotation: PsiAnnotation) : SpringMappingAnnotation(psiAnnotation) {

    override fun extractMethod(): String {
        return METHOD
    }

    companion object {
        private val METHOD = "GET"
    }

}
