package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;

import java.util.List;

public class TemplateRuleBlockTile extends TemplateRuleVanillaBlocks {

	public static final String PLUGIN_NAME = "blockTile";
	public NBTTagCompound tag;

	public TemplateRuleBlockTile(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		WorldTools.getTile(world, pos).ifPresent(t -> {
			tag = new NBTTagCompound();
			t.writeToNBT(tag);
			tag.removeTag("x");
			tag.removeTag("y");
			tag.removeTag("z");
		});
	}

	public TemplateRuleBlockTile(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		super.handlePlacement(world, turns, pos, builder);
		world.setBlockState(pos, BlockTools.rotateFacing(state, turns), 3);
		WorldTools.getTile(world, pos).ifPresent(t -> {
			//TODO look into changing this so that the whole TE doesn't need reloading from custom NBT
			tag.setString("id", state.getBlock().getRegistryName().toString());
			tag.setInteger("x", pos.getX());
			tag.setInteger("y", pos.getY());
			tag.setInteger("z", pos.getZ());
			t.readFromNBT(tag);
		});
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setTag("teData", this.tag);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		this.tag = tag.getCompoundTag("teData");
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
