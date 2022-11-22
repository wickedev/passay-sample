import org.passay.PasswordData
import org.passay.Rule
import org.passay.RuleResult


class RepeatStringsRule : Rule {
    companion object {
        const val ERROR_CODE = "ILLEGAL_REPEATED_STRINGS"
        const val DEFAULT_SEQUENCE_LENGTH = 3
        const val DEFAULT_MAX_SEQUENCE_COUNT = 1
    }

    private val sequenceLength: Int
    private val maxSequenceCount: Int
    private val repeats = mutableMapOf<String, Int>()

    constructor() : this(DEFAULT_SEQUENCE_LENGTH)

    constructor(sl: Int) : this(sl, DEFAULT_MAX_SEQUENCE_COUNT)

    constructor(sl: Int, maxSC: Int) {
        this.sequenceLength = sl
        this.maxSequenceCount = maxSC
    }

    override fun validate(passwordData: PasswordData): RuleResult {
        val result = RuleResult()
        val password = passwordData.password

        for (i in 0..password.length) {
            for (j in password.length downTo (i + 1)) {
                val length = (j - i)
                if (length != password.length && length >= sequenceLength) {
                    val sequence = password.substring(i, j)
                    val candidates = repeats.filter { it.value > maxSequenceCount }

                    if (sequence.hasSuperStringIn(candidates)) {
                        continue
                    }

                    val count = repeats[sequence] ?: 0
                    repeats[sequence] = count + 1
                }
            }
        }

        val invalidSequences = repeats.filter { it.value > maxSequenceCount }

        invalidSequences.forEach { (cSeq, count) ->
            result.addError(ERROR_CODE, createRuleResultDetailParameters(cSeq, count))
        }

        return result
    }

    private fun String.hasSuperStringIn(candidates: Map<String, Int>): Boolean {
        for (candidate in candidates) {
            if (isSubStringOf(candidate.key)) {
                return true
            }
        }

        return false
    }

    private fun String.isSubStringOf(s: String): Boolean {
        return this != s && s.indexOf(this) != -1
    }

    private fun createRuleResultDetailParameters(cSeq: CharSequence, repeatCount: Int): Map<String, Any> {
        return mapOf(
            "matchingCharacterSequence" to cSeq,
            "matchingRepeatCount" to repeatCount
        )
    }
}

