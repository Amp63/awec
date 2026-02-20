package amp.awec.util;

import java.util.Objects;

public class Vec3i {
	public int x, y, z;

	public Vec3i(int x, int y, int z) {
		set(x, y, z);
	}

	public void set(int x, int y, int z) {
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
		if (!(o instanceof Vec3i)) return false;
		Vec3i other = (Vec3i) o;
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	public Vec3i add(Vec3i other) {
		return new Vec3i(
			this.x + other.x,
			this.y + other.y,
			this.z + other.z
		);
	}

	public Vec3i subtract(Vec3i other) {
		return new Vec3i(
			this.x - other.x,
			this.y - other.y,
			this.z - other.z
		);
	}
}
