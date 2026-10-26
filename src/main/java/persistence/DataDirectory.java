package persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

/**
 * Prepares the directory that holds persisted cache records.
 *
 * The directory is created owner-only (0700) if missing. If it already exists it must
 * be a real directory (not a symlink) owned by the user running the server, so that
 * another local user cannot pre-create it (e.g. under /tmp) to plant records that are
 * recovered into the cache at startup, or to read the persisted data.
 */
public final class DataDirectory {

    private static final boolean POSIX =
            FileSystems.getDefault().supportedFileAttributeViews().contains("posix");

    private static final Set<PosixFilePermission> DIR_PERMS = PosixFilePermissions.fromString("rwx------");
    private static final Set<PosixFilePermission> FILE_PERMS = PosixFilePermissions.fromString("rw-------");

    private DataDirectory() {
    }

    public static File prepare(String dirName) throws IOException {
        Path dir = Paths.get(dirName).toAbsolutePath().normalize();
        if (Files.notExists(dir, LinkOption.NOFOLLOW_LINKS)) {
            Path parent = dir.getParent();
            if (parent != null)
                Files.createDirectories(parent);
            if (POSIX)
                Files.createDirectory(dir, PosixFilePermissions.asFileAttribute(DIR_PERMS));
            else
                Files.createDirectory(dir);
        }

        if (!Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS))
            throw new IOException("Data directory " + dir + " is not a directory (or is a symlink)");

        if (POSIX) {
            UserPrincipal owner = Files.getOwner(dir, LinkOption.NOFOLLOW_LINKS);
            UserPrincipal me = dir.getFileSystem().getUserPrincipalLookupService()
                    .lookupPrincipalByName(System.getProperty("user.name"));
            if (!owner.equals(me))
                throw new IOException("Data directory " + dir + " is owned by " + owner.getName()
                        + ", expected " + me.getName());
            Files.setPosixFilePermissions(dir, DIR_PERMS);
        }
        return dir.toFile();
    }

    /** Creates a new data file readable and writable only by the owner. */
    public static File createDataFile(File dir, String name) throws IOException {
        Path p = dir.toPath().resolve(name);
        if (POSIX)
            Files.createFile(p, PosixFilePermissions.asFileAttribute(FILE_PERMS));
        else
            Files.createFile(p);
        return p.toFile();
    }

    /** Regular (non-symlink) files in the data directory, never null. */
    public static File[] dataFiles(File dir) {
        File[] files = dir.listFiles(f -> Files.isRegularFile(f.toPath(), LinkOption.NOFOLLOW_LINKS));
        return files == null ? new File[0] : files;
    }
}
