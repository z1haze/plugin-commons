package me.z1haze.commons.update;

import java.io.IOException;

interface VersionSource {
    String latest() throws IOException;
}
