package com.bank.account.enums;

/**
 * Governs how multiple holders on a JOINT account can operate it.
 * See HolderRelation for who each person IS on the account;
 * OperationMode governs what they're COLLECTIVELY allowed to do.
 */
public enum OperationMode {
    SINGLE,                  // one holder, full control (INDIVIDUAL accounts)
    EITHER_OR_SURVIVOR,      // any one joint holder can operate independently
    JOINTLY,                 // ALL holders must approve every transaction
    FORMER_OR_SURVIVOR        // only the primary holder operates; other kicks in only if primary is unavailable
}