package amp.awec.util;

import java.util.Objects;

public class BlockPos {
	public final int x, y, z;

	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return this.x + ", " + this.y + ", " + this.z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BlockPos)) return false;
		BlockPos other = (BlockPos) o;
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	public BlockPos add(BlockPos other) {
		return new BlockPos(
			this.x + other.x,
			this.y + other.y,
			this.z + other.z
		);
	}

	public BlockPos subtract(BlockPos other) {
		return new BlockPos(
			this.x - other.x,
			this.y - other.y,
			this.z - other.z
		);
	}
}
