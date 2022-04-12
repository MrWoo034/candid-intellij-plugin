package com.github.alaanor.candid.reference

import com.github.alaanor.candid.psi.CandidElementFactory
import com.github.alaanor.candid.psi.CandidIdentifierDeclaration
import com.github.alaanor.candid.psi.CandidIdentifierReference
import com.github.alaanor.candid.psi.impl.CandidDefinitionImpl
import com.intellij.codeInsight.daemon.EmptyResolveMessageProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil

class CandidTypeReference : PsiReferenceBase<CandidIdentifierReference>, EmptyResolveMessageProvider {

    private var textRange: TextRange

    constructor(identifierReference: CandidIdentifierReference, textRange: TextRange) : super(identifierReference, textRange) {
        this.textRange = textRange
    }

    override fun calculateDefaultRangeInElement(): TextRange = element.textRange
    override fun getRangeInElement(): TextRange = textRange
    override fun getUnresolvedMessagePattern(): String = "Unresolved type"

    override fun resolve(): PsiElement? {
        return PsiTreeUtil.findChildrenOfType(element.containingFile, CandidIdentifierDeclaration::class.java)
            .find { element.text == it.text }
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        when (element) {
            is CandidDefinitionImpl ->
                if (element.nameIdentifier?.text == this.element.text)
                    return true
        }
        return false
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val newElement = CandidElementFactory.createTypeReference(element.project, newElementName)
        return element.replace(newElement)
    }

}