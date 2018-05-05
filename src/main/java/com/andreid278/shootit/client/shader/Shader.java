package com.andreid278.shootit.client.shader;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public class Shader {
	public String name;
	public ResourceLocation rl;
	public List<UniformInfo> uniformList;
	
	public Shader(String name) {
		this(name, null);
	}
	
	public Shader(String name, ResourceLocation rl) {
		this.name = name;
		this.rl = rl;
		uniformList = new ArrayList<>();
	}
	
	public void addUniform(String name, float min, float max, float initValue, float step) {
		uniformList.add(new UniformInfo(name, min, max, initValue, step));
	}
	
	public class UniformInfo {
		public String name;
		public float min;
		public float max;
		public float initValue;
		public float step;
		
		public UniformInfo(String name, float min, float max, float initValue, float step) {
			this.name = name;
			this.min = min;
			this.max = max;
			this.initValue = initValue;
			this.step = step;
		}
	}
}
