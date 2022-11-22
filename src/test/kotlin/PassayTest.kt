import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import org.passay.PasswordData
import org.passay.PasswordValidator
import org.passay.PropertiesMessageResolver
import org.passay.RuleResult

class PassayTest : DescribeSpec({
    val validator = PasswordValidator(
        PropertiesMessageResolver(PropertiesMessageResolver.getDefaultProperties().apply {
            put(RepeatStringsRule.ERROR_CODE, "The password contains the string '%1\$s' repeated %2\$s times.")
        }),
        RepeatStringsRule(2, 1),
    )

    describe("passay validator") {
        it("validate with invalid string") {
            val password = "asdfasdfzxcvzxcv"
            val result: RuleResult = validator.validate(PasswordData(password))
            val errorMessages = validator.getMessages(result)
            errorMessages.size.shouldBe(2)
            result.isValid.shouldBeFalse()
            println(errorMessages)
        }
    }
})