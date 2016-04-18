package me.timetabler.installer;

/**
 * The operating systems and architectures currently supported by the program.
 */
public enum OperatingSystem {
    /**
     * Windows 64 bit.
     */
    WINDOWS_X64,

    /**
     * Windows 32 bit.
     */
    WINDOWS_X86,

    /**
     * Linux or Unix 32 bit.
     */
    LINUX_X86,

    /**
     * Linux or Unix 64 bit.
     */
    LINUX_X64;

    /**
     * The current operating system. This is initialised by {@link #getCurrentOs}.
     */
    private static OperatingSystem current;

    /**
     * Returns the current operating system, or throws an UnsupportedOperationException if the current operating system
     * is not supported.
     * @return The current operating system.
     * @throws UnsupportedOperationException Thrown if the current operating system is not supported by the timetabler.
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
                    StringBuilder builder = new StringBuilder();
                    builder.append("Unsupported Operating System! Please Use One Of The Following [");
                    for (OperatingSystem operatingSystem : OperatingSystem.values()) {
                        builder.append(operatingSystem.toString()).append(',');
                    }
                    builder.deleteCharAt(builder.lastIndexOf(","));
                    builder.append(']');
                    throw new UnsupportedOperationException(builder.toString());
                }
            } else if (osArch.contains("86")) {
                if (osName.contains("win")) {
                    current = WINDOWS_X86;
                } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aux")) {
                    current = LINUX_X86;
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Unsupported Operating System! Please Use One Of The Following [");
                    for (OperatingSystem operatingSystem : OperatingSystem.values()) {
                        builder.append(operatingSystem.toString()).append(',');
                    }
                    builder.deleteCharAt(builder.lastIndexOf(","));
                    builder.append(']');
                    throw new UnsupportedOperationException(builder.toString());
                }
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("Unsupported System Architecture! Please Use One Of The Following [");
                for (OperatingSystem operatingSystem : OperatingSystem.values()) {
                    builder.append(operatingSystem.toString()).append(',');
                }
                builder.deleteCharAt(builder.lastIndexOf(","));
                builder.append(']');
                throw new UnsupportedOperationException(builder.toString());
            }
        }

        return current;
    }
}
