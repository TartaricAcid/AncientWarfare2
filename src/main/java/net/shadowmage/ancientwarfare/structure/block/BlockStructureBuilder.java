package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.render.RenderStructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockStructureBuilder extends BlockBaseStructure {

	private NonNullList<ItemStack> displayCache = null;

	public BlockStructureBuilder() {
		super(Material.ROCK, "structure_builder_ticked");
		setHardness(2.f);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (displayCache == null || displayCache.isEmpty()) {
			displayCache = NonNullList.create();

			//TODO rework structure template manager so that it keeps only one central repository that either is already filled on server or gets updated on client.
			Set<String> templateNames;
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
				templateNames = StructureTemplateManager.INSTANCE.getSurvivalStructures().keySet();
			} else {
				templateNames = StructureTemplateManagerClient.instance().getSurvivalStructures().stream().map(t -> t.name).collect(Collectors.toSet());
			}
			@Nonnull ItemStack item;
			for (String templateName : templateNames) {
				item = new ItemStack(this);
				item.setTagInfo("structureName", new NBTTagString(templateName));
				displayCache.add(item);
			}

		}
		if (!displayCache.isEmpty()) {
			items.addAll(displayCache);
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileStructureBuilder();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			WorldTools.getTile(world, pos, TileStructureBuilder.class).ifPresent(b -> b.onBlockClicked(player));
		}
		return true;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack drop = new ItemStack(this);
		WorldTools.getTile(world, pos, TileStructureBuilder.class)
				.ifPresent(t -> drop.setTagInfo("structureName", new NBTTagString(t.getBuilder().getTemplate().name)));
		drops.add(drop);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		//If it will harvest, delay deletion of the block until after getDrops
		return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
		super.harvestBlock(world, player, pos, state, te, tool);
		world.setBlockToAir(pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		ClientRegistry.bindTileEntitySpecialRenderer(TileStructureBuilder.class, new RenderStructureBuilder());
	}
}
