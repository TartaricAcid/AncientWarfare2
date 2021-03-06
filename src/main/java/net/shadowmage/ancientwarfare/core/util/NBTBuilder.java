package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.NBTTagCompound;

public class NBTBuilder {
	private NBTTagCompound tag = new NBTTagCompound();

	public NBTBuilder setString(String key, String value) {
		tag.setString(key, value);
		return this;
	}

	public NBTBuilder setBoolean(String key, boolean value) {
		tag.setBoolean(key, value);
		return this;
	}

	public NBTTagCompound build() {
		return tag;
	}
}
