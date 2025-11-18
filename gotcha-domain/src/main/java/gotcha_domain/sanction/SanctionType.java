package gotcha_domain.sanction;

import gotcha_domain.user.User;

public enum SanctionType {
    WARNING {
        @Override
        public void apply(User user, Long days) {
            user.incrementWarningCount();
        }
    },
    TEMP_BAN {
        @Override
        public void apply(User user, Long days) {
            user.suspendUser(days);
        }
    },
    TEMP_BAN_CANCEL {
        @Override
        public void apply(User user, Long days) {
            user.unsuspendUser(); // 정지 취소(즉시 해제)
        }
    },
    PERM_BAN {
        @Override
        public void apply(User user, Long days) {
            user.banUser();
        }
    };

    public abstract void apply(User user, Long days);
}
