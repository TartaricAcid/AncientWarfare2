package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcVikingArcherElite extends NpcFactionArcher
{

public NpcVikingArcherElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "viking.archer";
  }

@Override
public String getNpcSubType()
  {
  return "elite";
  }

}