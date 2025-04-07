/**
 * Copyright (C) 2025  Nebojša Majić (Onako2)
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see
 * <https://www.gnu.org/licenses/>.
 */

package rs.onako2.ouroverlay.json;

import com.google.gson.annotations.Expose;

public class ConfigJson {
    @Expose
    public int version;
    @Expose
    public double zoom;
    @Expose
    public String url;

    public ConfigJson(int version, double zoom, String url) {
        this.version = version;
        this.zoom = zoom;
        this.url = url;
    }
}
