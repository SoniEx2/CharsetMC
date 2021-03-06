package pl.asie.charset.gates;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import pl.asie.charset.lib.ModCharsetLib;

public class ItemGate extends ItemMultiPart {
	public ItemGate() {
		setHasSubtypes(true);
		setCreativeTab(ModCharsetLib.CREATIVE_TAB);
	}

	public static ItemStack getStack(PartGate gate) {
		return getStack(gate, false);
	}

	public static ItemStack getStack(PartGate gate, boolean silky) {
		ItemStack stack = new ItemStack(ModCharsetGates.itemGate, 1, ModCharsetGates.metaGate.get(gate.getType().toString()));
		stack.setTagCompound(new NBTTagCompound());
		gate.writeItemNBT(stack.getTagCompound(), silky);
		return stack;
	}

	public static PartGate getPartGate(ItemStack stack) {
		PartGate gate = getPartGate(stack.getItemDamage());
		if (gate != null && stack.hasTagCompound()) {
			gate.readItemNBT(stack.getTagCompound());
		}
		return gate;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		PartGate gate = getPartGate(stack);
		String name = ModCharsetGates.gateUN[stack.getItemDamage() % ModCharsetGates.gateUN.length];
		if (gate.isSideInverted(EnumFacing.NORTH) && I18n.canTranslate(name + ".i")) {
			name += ".i";
		}
		return I18n.translateToLocal(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (ItemStack stack : ModCharsetGates.gateStacks) {
			subItems.add(stack);
		}
	}

	@Override
	public boolean place(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
		if (!world.isSideSolid(pos.offset(side), side.getOpposite())) {
			return false;
		}

		return super.place(world, pos, side, hit, stack, player);
	}

	public static PartGate getPartGate(int meta) {
		try {
			return ModCharsetGates.gateParts.get(ModCharsetGates.gateMeta[meta % ModCharsetGates.gateMeta.length]).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing facing, Vec3d hit, ItemStack stack, EntityPlayer player) {
		PartGate part = getPartGate(stack);
		return part != null ? part.setSide(facing).setTop(facing.getAxis() == EnumFacing.Axis.Y ? player.getHorizontalFacing() : EnumFacing.NORTH) : null;
	}
}
