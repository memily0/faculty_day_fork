package homework

/**
 * Bank account with deterministic lock ordering, so opposite transfers cannot deadlock.
 */
class BankAccount(val id: String, var balance: Int) {

    fun transfer(to: BankAccount, amount: Int) {
        require(amount >= 0) { "Amount must be non-negative" }
        if (this === to || amount == 0) {
            return
        }

        val orderedAccounts = ordered(this, to)
        if (orderedAccounts == null) {
            synchronized(tieLock) {
                synchronized(this) {
                    synchronized(to) {
                        transferLocked(to, amount)
                    }
                }
            }
            return
        }

        val (first, second) = orderedAccounts
        synchronized(first) {
            synchronized(second) {
                transferLocked(to, amount)
            }
        }
    }

    private fun transferLocked(to: BankAccount, amount: Int) {
        if (balance >= amount) {
            balance -= amount
            to.balance += amount
        }
    }

    private companion object {
        private val tieLock = Any()

        private fun ordered(
            left: BankAccount,
            right: BankAccount
        ): Pair<BankAccount, BankAccount>? {
            val idComparison = left.id.compareTo(right.id)
            if (idComparison < 0) {
                return left to right
            }
            if (idComparison > 0) {
                return right to left
            }

            val leftHash = System.identityHashCode(left)
            val rightHash = System.identityHashCode(right)
            if (leftHash < rightHash) {
                return left to right
            }
            if (leftHash > rightHash) {
                return right to left
            }

            return null
        }
    }
}
