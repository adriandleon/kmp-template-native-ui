package com.adriandeleon.kmp.template.onboarding

import com.adriandeleon.kmp.template.common.util.testComponentContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class DefaultOnboardingComponentTest :
    FunSpec({
        test("starts on the welcome page") {
            val component = onboardingComponent()

            component.pageOrder() shouldContainExactly
                listOf(
                    OnboardingComponent.Page.Welcome,
                    OnboardingComponent.Page.Organize,
                    OnboardingComponent.Page.Customize,
                )
            component.state.value.selectedPage shouldBe OnboardingComponent.Page.Welcome
            component.state.value.selectedIndex shouldBe 0
        }

        test("next selects the following page") {
            val component = onboardingComponent()

            component.next()

            component.state.value.selectedPage shouldBe OnboardingComponent.Page.Organize
            component.pages.value.selectedIndex shouldBe 1
        }

        test("previous selects the prior page") {
            val component = onboardingComponent()

            component.next()
            component.previous()

            component.state.value.selectedPage shouldBe OnboardingComponent.Page.Welcome
            component.pages.value.selectedIndex shouldBe 0
        }

        test("previous remains on the first page") {
            val component = onboardingComponent()

            component.previous()

            component.state.value.selectedPage shouldBe OnboardingComponent.Page.Welcome
            component.pages.value.selectedIndex shouldBe 0
        }

        test("next remains on the last page") {
            val component = onboardingComponent()

            component.next()
            component.next()
            component.next()

            component.state.value.selectedPage shouldBe OnboardingComponent.Page.Customize
            component.pages.value.selectedIndex shouldBe 2
        }

        test("skip emits completed output") {
            val outputs = mutableListOf<OnboardingComponent.Output>()
            val component = onboardingComponent(onOutput = outputs::add)

            component.skip()

            outputs shouldContainExactly listOf(OnboardingComponent.Output.Completed)
        }

        test("finish emits completed output from the last page") {
            val outputs = mutableListOf<OnboardingComponent.Output>()
            val component = onboardingComponent(onOutput = outputs::add)

            component.next()
            component.next()
            component.finish()

            outputs shouldContainExactly listOf(OnboardingComponent.Output.Completed)
        }

        test("finish advances until the last page before emitting output") {
            val outputs = mutableListOf<OnboardingComponent.Output>()
            val component = onboardingComponent(onOutput = outputs::add)

            component.finish()

            outputs shouldBe emptyList()
            component.state.value.selectedPage shouldBe OnboardingComponent.Page.Organize

            component.finish()

            outputs shouldBe emptyList()
            component.state.value.selectedPage shouldBe OnboardingComponent.Page.Customize

            component.finish()

            outputs shouldContainExactly listOf(OnboardingComponent.Output.Completed)
        }
    })

private fun onboardingComponent(
    onOutput: (OnboardingComponent.Output) -> Unit = {}
): OnboardingComponent =
    DefaultOnboardingComponent(componentContext = testComponentContext(), onOutput = onOutput)

private fun OnboardingComponent.pageOrder(): List<OnboardingComponent.Page> =
    pages.value.items.map { it.configuration }
