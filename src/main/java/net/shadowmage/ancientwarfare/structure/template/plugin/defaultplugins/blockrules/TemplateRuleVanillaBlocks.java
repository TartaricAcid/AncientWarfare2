package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;

public class TemplateRuleVanillaBlocks extends TemplateRuleBlock {

	public static final String PLUGIN_NAME = "vanillaBlocks";
	public int buildPass;

	/*
	 * constructor for dynamic construction.  passed world and coords so that the rule can handle its own logic internally
	 */
	public TemplateRuleVanillaBlocks(World world, BlockPos pos, IBlockState state, int turns) {
		super(state, turns);
		this.buildPass = BlockDataManager.INSTANCE.getPriorityForBlock(state.getBlock());
	}

	public TemplateRuleVanillaBlocks(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		builder.placeBlock(pos, BlockTools.rotateFacing(state, turns), buildPass);
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return state.getBlock() == this.state.getBlock() && state.getProperties().equals(BlockTools.rotateFacing(this.state, turns).getProperties());
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == this.buildPass;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setInteger("buildPass", buildPass);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		this.buildPass = tag.getInteger("buildPass");
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
