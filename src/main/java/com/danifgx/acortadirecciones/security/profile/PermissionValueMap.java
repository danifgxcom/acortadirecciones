package com.danifgx.acortadirecciones.security.profile;

import java.util.EnumSet;

public class PermissionValueMap {

    private static final EnumSet<Permission> LENGTH_32 = EnumSet.of(Permission.URL_LENGTH_32);
    private static final EnumSet<Permission> LENGTH_24 = EnumSet.of(Permission.URL_LENGTH_24);
    private static final EnumSet<Permission> LENGTH_16 = EnumSet.of(Permission.URL_LENGTH_16);
    private static final EnumSet<Permission> LENGTH_8 = EnumSet.of(Permission.URL_LENGTH_8);

    private static final EnumSet<Permission> EXPIRY_24H = EnumSet.of(Permission.EXP_24H);
    private static final EnumSet<Permission> EXPIRY_48H = EnumSet.of(Permission.EXP_48H);
    private static final EnumSet<Permission> EXPIRY_72H = EnumSet.of(Permission.EXP_72H);

    private static final EnumSet<Permission> MANAGE_USERS = EnumSet.of(Permission.MANAGE_USERS);

    public static class LengthOption {
        public final int length;
        public final EnumSet<Permission> permissions;

        public LengthOption(int length, EnumSet<Permission> permissions) {
            this.length = length;
            this.permissions = permissions;
        }
    }

    public static class ExpiryOption {
        public final int hours;
        public final EnumSet<Permission> permissions;

        public ExpiryOption(int hours, EnumSet<Permission> permissions) {
            this.hours = hours;
            this.permissions = permissions;
        }
    }

    public static final LengthOption LENGTH_OPTION_32 = new LengthOption(32, LENGTH_32);
    public static final LengthOption LENGTH_OPTION_24 = new LengthOption(24, LENGTH_24);
    public static final LengthOption LENGTH_OPTION_16 = new LengthOption(16, LENGTH_16);
    public static final LengthOption LENGTH_OPTION_8 = new LengthOption(8, LENGTH_8);

    public static final ExpiryOption EXPIRY_OPTION_24H = new ExpiryOption(24, EXPIRY_24H);
    public static final ExpiryOption EXPIRY_OPTION_48H = new ExpiryOption(48, EXPIRY_48H);
    public static final ExpiryOption EXPIRY_OPTION_72H = new ExpiryOption(72, EXPIRY_72H);

    public static final ExpiryOption ADMIN_MANAGE_USERS = new ExpiryOption(72, MANAGE_USERS);
}
