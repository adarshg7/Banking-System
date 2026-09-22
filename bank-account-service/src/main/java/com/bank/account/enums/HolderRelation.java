package com.bank.account.enums;

/**
 * WHO a person is in relation to a specific account.
 * Distinct from OperationMode (which governs HOW they can act together).
 */
public enum HolderRelation {
    OWNER,           // primary account owner
    JOINT_HOLDER,    // co-owner in a JOINT account
    GUARDIAN,        // operates on behalf of a MINOR owner
    NOMINEE,         // inherits account in case of owner's death — no operating rights while owner is alive
    AUTHORIZED_SIGNATORY  // for BUSINESS/CORPORATE accounts — can operate per company mandate
}