package com.andreid278.shootit.Misc;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.filefilter.DirectoryFileFilter;

import com.google.common.collect.Sets;

import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class CustomFolderResourcePack extends FolderResourcePack {
	public CustomFolderResourcePack(File resourcePackFileIn) {
		super(resourcePackFileIn);
	}
	
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
		return null;
	}
}
