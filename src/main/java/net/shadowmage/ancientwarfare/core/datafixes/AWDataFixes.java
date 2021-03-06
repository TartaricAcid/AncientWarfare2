package net.shadowmage.ancientwarfare.core.datafixes;

import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.shadowmage.ancientwarfare.npc.datafixes.FactionEntityFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.FactionExpansionEntityFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.FactionExpansionItemFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.FactionSpawnerItemFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.RoutingOrderFilterCountsFixer;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;

import static net.shadowmage.ancientwarfare.core.AncientWarfareCore.MOD_ID;

public class AWDataFixes {
	private AWDataFixes() {}

	private static final int DATA_FIXER_VERSION = 6;

	public static void registerDataFixes() {
		CompoundDataFixer dataFixer = FMLCommonHandler.instance().getDataFixer();
		AWNPCEntities.getNpcMap().values().forEach(npc -> NpcBase.registerFixesNpc(dataFixer, npc.getEntityClass()));
		ModFixs fixes = dataFixer.init(MOD_ID, DATA_FIXER_VERSION);
		fixes.registerFix(FixTypes.ENTITY, new VehicleOwnerFixer());
		fixes.registerFix(FixTypes.BLOCK_ENTITY, new TileOwnerFixer());
		fixes.registerFix(FixTypes.BLOCK_ENTITY, new TileIdFixer());
		fixes.registerFix(FixTypes.ENTITY, new FactionEntityFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new FactionSpawnerItemFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new ResearchNoteFixer());
		fixes.registerFix(FixTypes.ENTITY, new FactionExpansionEntityFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new FactionExpansionItemFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new RoutingOrderFilterCountsFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new RoutingOrderFilterCountsFixer());
	}
}
