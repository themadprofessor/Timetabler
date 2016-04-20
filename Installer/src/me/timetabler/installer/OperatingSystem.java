package me.timetabler.installer;

/**
 * The operating systems and architectures currently supported by the program.
 */
public enum OperatingSystem {
    /**
     * Windows 64 bit.
     */
    WINDOWS_X64("windows-x86_64"),

    /**
     * Windows 32 bit.
     */
    WINDOWS_X86("windows-i686"),

    /**
     * Linux or Unix 32 bit.
     */
    LINUX_X86("linux-i686"),

    /**
     * Linux or Unix 64 bit.
     */
    LINUX_X64("linux-x86_64"),

    /**
     * An unsupported operating system and/or architecture.
     */
    UNSUPPORTED(null);

    /**
     * The current operating system. This is initialised by {@link #getCurrentOs}.
     */
    private static OperatingSystem current;

    /**
     * The name the operating system and architecture.
     */
    private String name;

    /**
     * The private constructor for this enum.
     * @param name The name of the operating system and architecture.
     */
    OperatingSystem(String name) {
        this.name = name;
    }

    /**
     * Returns the name of operating system and architecture, or null for {@link #UNSUPPORTED}. It will be in the form
     * <i>operating system</i>-<i>architecture</i>. For example, WINDOWS_X64 will return windows-x86_64 and WINDOWS_X86
     * will return windows-i686.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current operating system, which can be {@link #UNSUPPORTED} if the current operating system is
     * unsupported by the timetabler.
     * @return The current operating system, which can be {@link #UNSUPPORTED}.
     */
    public static OperatingSystem getCurrentOs() {
        //Use lazy initialisation as the os will not current during one run.
        if (current == null) {
            String osName = System.getProperty("os.name").toLowerCase();
            String osArch = System.getProperty("os.arch");

            if (osArch.contains("64")) {
                if (osName.contains("win")) {
                    current = WINDOWS_X64;
                } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aux")) {
                    current = LINUX_X64;
                } else {
                    /*StringBuilder builder = new StringBuilder();
                    builder.append("Unsupported Operating System! Please Use One Of The Following [");
                    for (OperatingSystem operatingSystem : OperatingSystem.values()) {
                        builder.append(operatingSystem.toString()).append(',');
                    }
                    builder.deleteCharAt(builder.lastIndexOf(","));
                    builder.append(']');
                    throw new UnsupportedOperationException(builder.toString());*/
                    current = UNSUPPORTED;
                }
            } else if (osArch.contains("86")) {
                if (osName.contains("win")) {
                    current = WINDOWS_X86;
                } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aux")) {
                    current = LINUX_X86;
                } else {
                    current = UNSUPPORTED;
                }
            } else {
                current = UNSUPPORTED;
            }
        }

        return current;
    }
}
