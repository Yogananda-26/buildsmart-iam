package com.buildsmart.analytics.client;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback for IamServiceClient when the IAM module is unavailable.
 * Returns a user with ACTIVE status by default so the system doesn't block
 * all users when IAM is temporarily down (fail-open approach).
 */
@Component
public class IamServiceFallback implements IamServiceClient {

    private static final Logger log = LoggerFactory.getLogger(IamServiceFallback.class);

    private static final List<IamUserDTO> FALLBACK_USERS = List.of(
            new IamUserDTO("BSAD001", "System Administrator", "admin@buildsmart.com", "0000000000", "ADMIN", "ACTIVE", null, null),
            new IamUserDTO("BSPM001", "Arjun Mehta", "arjun.mehta@buildsmart.com", "9876543210", "PROJECT_MANAGER", "ACTIVE", null, null),
            new IamUserDTO("BSPM002", "Sneha Iyer", "sneha.iyer@buildsmart.com", "9876543211", "PROJECT_MANAGER", "ACTIVE", null, null),
            new IamUserDTO("BSPM003", "Deepak Joshi", "deepak.joshi@buildsmart.com", "9876543212", "PROJECT_MANAGER", "INACTIVE", null, null),
            new IamUserDTO("BSSE001", "Rajesh Kumar", "rajesh.kumar@buildsmart.com", "9876543213", "SITE_ENGINEER", "ACTIVE", null, null),
            new IamUserDTO("BSSE002", "Anil Sharma", "anil.sharma@buildsmart.com", "9876543214", "SITE_ENGINEER", "ACTIVE", null, null),
            new IamUserDTO("BSSE003", "Priya Patel", "priya.patel@buildsmart.com", "9876543215", "SITE_ENGINEER", "ACTIVE", null, null),
            new IamUserDTO("BSSE004", "Vikram Singh", "vikram.singh@buildsmart.com", "9876543216", "SITE_ENGINEER", "SUSPENDED", null, null),
            new IamUserDTO("BSSE005", "Meena Reddy", "meena.reddy@buildsmart.com", "9876543217", "SITE_ENGINEER", "ACTIVE", null, null),
            new IamUserDTO("BSSE006", "Suresh Nair", "suresh.nair@buildsmart.com", "9876543218", "SITE_ENGINEER", "INACTIVE", null, null),
            new IamUserDTO("BSWK001", "Ramesh Yadav", "ramesh.yadav@buildsmart.com", "9876543219", "WORKER", "ACTIVE", null, null),
            new IamUserDTO("BSWK002", "Kiran Desai", "kiran.desai@buildsmart.com", "9876543220", "WORKER", "ACTIVE", null, null),
            new IamUserDTO("BSWK003", "Manish Gupta", "manish.gupta@buildsmart.com", "9876543221", "WORKER", "SUSPENDED", null, null),
            new IamUserDTO("BSWK004", "Pooja Tiwari", "pooja.tiwari@buildsmart.com", "9876543222", "WORKER", "ACTIVE", null, null),
            new IamUserDTO("BSWK005", "Sunita Verma", "sunita.verma@buildsmart.com", "9876543223", "WORKER", "INACTIVE", null, null),
            new IamUserDTO("BSWK006", "Naveen Pillai", "naveen.pillai@buildsmart.com", "9876543224", "WORKER", "ACTIVE", null, null),
            new IamUserDTO("BSWK007", "Divya Saxena", "divya.saxena@buildsmart.com", "9876543225", "WORKER", "ACTIVE", null, null),
            new IamUserDTO("BSWK008", "Ravi Malhotra", "ravi.malhotra@buildsmart.com", "9876543226", "WORKER", "SUSPENDED", null, null),
            new IamUserDTO("BSVN001", "Amit Kapoor", "amit.kapoor@buildsmart.com", "9876543227", "VENDOR", "ACTIVE", null, null),
            new IamUserDTO("BSVN002", "Lakshmi Nair", "lakshmi.nair@buildsmart.com", "9876543228", "VENDOR", "INACTIVE", null, null)
    );

    @Override
    public IamApiResponse<List<IamUserDTO>> getAllUsersWrapped() {
        log.warn("IAM service unavailable — returning fallback user list");
        return new IamApiResponse<>(true, "Fallback data", FALLBACK_USERS);
    }

    @Override
    public IamApiResponse<IamUserDTO> getUserProfile() {
        log.warn("IAM service unavailable — returning fallback profile");
        return new IamApiResponse<>(true, "Fallback data", FALLBACK_USERS.get(0));
    }


}
