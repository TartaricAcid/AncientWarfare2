package net.shadowmage.ancientwarfare.structure.template.scan;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TemplateScanner {

    /*
	 * @param turns # of turns for proper orientation
     */

	public static StructureTemplate scan(World world, BlockPos min, BlockPos max, BlockPos key, int turns, String name) {
		int xSize = max.getX() - min.getX() + 1;
		int ySize = max.getY() - min.getY() + 1;
		int zSize = max.getZ() - min.getZ() + 1;

		int xOutSize = xSize;
		int zOutSize = zSize;
		int swap;
		for (int i = 0; i < turns; i++) {
			swap = xOutSize;
			xOutSize = zOutSize;
			zOutSize = swap;
		}
		key = BlockTools.rotateInArea(key.subtract(min.add(0, -1, 0)), xSize, zSize, turns);

		short[] templateRuleData = new short[xSize * ySize * zSize];

		HashMap<String, List<TemplateRuleBlock>> pluginBlockRuleMap = new HashMap<>();
		Map<Integer, TemplateRule> blockRules = new HashMap<>();
		Block scannedBlock;
		List<TemplateRuleBlock> pluginBlockRules;
		int index;
		int meta;
		int nextRuleID = 1;
		BlockPos destination;
		for (int scanY = min.getY(); scanY <= max.getY(); scanY++) {
			for (int scanZ = min.getZ(); scanZ <= max.getZ(); scanZ++) {
				for (int scanX = min.getX(); scanX <= max.getX(); scanX++) {
					destination = BlockTools.rotateInArea(new BlockPos(scanX, scanY, scanZ).subtract(min), xSize, zSize, turns);

					BlockPos scannedPos = new BlockPos(scanX, scanY, scanZ);
					IBlockState scannedState = world.getBlockState(scannedPos);
					scannedBlock = scannedState.getBlock();

					if (!AWStructureStatics.shouldSkipScan(scannedBlock) && !world.isAirBlock(scannedPos)) {
						Optional<String> pluginId = StructurePluginManager.INSTANCE.getPluginNameFor(scannedBlock);
						if (pluginId.isPresent()) {
							pluginBlockRules = pluginBlockRuleMap.computeIfAbsent(pluginId.get(), k -> new ArrayList<>());
							Optional<TemplateRuleBlock> scannedBlockRule = Optional.empty();
							for (TemplateRuleBlock rule : pluginBlockRules) {
								if (rule.shouldReuseRule(world, scannedState, turns, scannedPos)) {
									scannedBlockRule = Optional.of(rule);
									break;
								}
							}
							if (!scannedBlockRule.isPresent()) {
								scannedBlockRule = StructurePluginManager.INSTANCE.getRuleForBlock(world, scannedState, turns, scannedPos);
								if (scannedBlockRule.isPresent()) {
									scannedBlockRule.get().ruleNumber = nextRuleID;
									nextRuleID++;
									pluginBlockRules.add(scannedBlockRule.get());
									blockRules.put(scannedBlockRule.get().ruleNumber, scannedBlockRule.get());
								}
							}
							index = StructureTemplate.getIndex(destination, new Vec3i(xOutSize, ySize, zOutSize));
							templateRuleData[index] = scannedBlockRule.map(r -> (short) r.ruleNumber).orElse((short) -1);
						}
					}
				}//end scan x-level for
			}//end scan z-level for
		}//end scan y-level for

		Map<Integer, TemplateRuleEntity> entityRules = new HashMap<>();
		List<Entity> entitiesInAABB = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1));
		nextRuleID = 0;
		for (Entity e : entitiesInAABB) {
			int ex = MathHelper.floor(e.posX);
			int ey = MathHelper.floor(e.posY);
			int ez = MathHelper.floor(e.posZ);
			Optional<TemplateRuleEntity> scannedEntityRule = StructurePluginManager.INSTANCE.getRuleForEntity(world, e, turns, ex, ey, ez);
			if (scannedEntityRule.isPresent()) {
				destination = BlockTools.rotateInArea(new BlockPos(ex, ey, ez).subtract(min), xSize, zSize, turns);
				scannedEntityRule.get().ruleNumber = nextRuleID;
				scannedEntityRule.get().setPosition(destination);
				entityRules.put(nextRuleID, scannedEntityRule.get());
				nextRuleID++;
			}
		}

		StructureTemplate template = new StructureTemplate(name, new Vec3i(xOutSize, ySize, zOutSize), key);
		template.setTemplateData(templateRuleData);
		template.setBlockRules(blockRules);
		template.setEntityRules(entityRules);
		return template;
	}
}
