/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package javax.microedition.m3g;

public class VertexBuffer extends Object3D
{

	// The `fixed` field represents whether or not the vertex count (`length`)
	// of this `VertexBuffer` has been determined.
	//
	// The first `VertexArray` added to this `VertexBuffer` makes
	// it "fixed", and the `length` will be set to the number
	// of vertices in the `VertexArray`.
	//
	// Once the `VertexBuffer` is fixed, it only accepts `VertexArray`s
	// with exactly `length` vertices.
	private boolean fixed;
	private int length;
	private int defaultColor;
	private VertexArray positions;
	private VertexArray normals;
	private VertexArray colors;
	private VertexArray[] texCoords;

	private float positionScale;
	private float[] positionBias;
	private float[] texCoordScale;
	private float[][] texCoordBias;
	// colorScale =   1/255
	// colorBias  = 128/255


	public VertexBuffer()
	{
		this.fixed = false;
		this.length = 0;
		this.defaultColor = 0xffffffff;
		this.positions = null;
		this.normals = null;
		this.colors = null;
		this.texCoords = new VertexArray[Graphics3D.NUM_TEXTURE_UNITS];
		this.texCoordScale = new float[Graphics3D.NUM_TEXTURE_UNITS];
		this.texCoordBias = new float[Graphics3D.NUM_TEXTURE_UNITS][0];
	}


	public VertexArray getColors()
	{
		return this.colors;
	}

	public int getDefaultColor()
	{
		return this.defaultColor;
	}

	public VertexArray getNormals()
	{
		return this.normals;
	}

	public VertexArray getPositions(float[] scaleBias)
	{
		if (scaleBias == null)
			return this.positions;

		if (scaleBias.length < 4)
			throw new java.lang.IllegalArgumentException();

		scaleBias[0] = this.positionScale;
		for (int i = 0; i < 3; i++)
			scaleBias[i + 1] = this.positionBias[i];

		return this.positions;
	}

	public VertexArray getTexCoords(int index, float[] scaleBias)
	{
		if (index < 0 || Graphics3D.NUM_TEXTURE_UNITS - 1 < index)
			throw new java.lang.IndexOutOfBoundsException();

		if (scaleBias == null)
			return this.texCoords[index];

		if (scaleBias.length < this.texCoords[index].getVertexCount() + 1)
			throw new java.lang.IllegalArgumentException();

		scaleBias[0] = this.texCoordScale[index];
		for (int i = 0; i < this.texCoords[index].getVertexCount(); i++)
			scaleBias[i + 1] = this.texCoordBias[index][i];

		return this.texCoords[index];
	}

	public int getVertexCount()
	{
		return this.length;
	}

	public void setColors(VertexArray colors)
	{
		if (colors == null)
		{
			this.colors = null;
			return;
		}

		if (colors.getComponentType() != 1 ||
			colors.getComponentCount() < 3 || 4 < colors.getComponentCount() ||
			(this.fixed && colors.getVertexCount() != this.length))
			throw new java.lang.IllegalArgumentException();

		this.updateLength(colors.getVertexCount());
		this.colors = colors;
	}

	public void setDefaultColor(int ARGB)
	{
		this.defaultColor = ARGB;
	}

	public void setNormals(VertexArray normals)
	{
		if (normals == null)
		{
			this.normals = null;
			return;
		}

		if (normals.getComponentCount() != 3 ||
			(this.fixed && normals.getVertexCount() != this.length))
			throw new java.lang.IllegalArgumentException();

		this.updateLength(normals.getVertexCount());
		this.normals = normals;
	}

	public void setPositions(VertexArray positions, float scale, float[] bias)
	{
		if (positions == null)
		{
			this.positions = null;
			return;
		}

		if (positions.getComponentCount() != 3 ||
			(this.fixed && positions.getVertexCount() != this.length) ||
			(bias != null && bias.length < 3))
			throw new java.lang.IllegalArgumentException();

		if (bias == null)
			bias = new float[3];

		this.updateLength(positions.getVertexCount());
		this.positions = positions;
		this.positionScale = scale;
		this.positionBias = bias;
	}

	public void setTexCoords(
		int index,
		VertexArray texCoords,
		float scale,
		float[] bias
	) {
		if (index < 0 || Graphics3D.NUM_TEXTURE_UNITS - 1 < index)
			throw new java.lang.IndexOutOfBoundsException();

		if (texCoords == null) {
			this.texCoords[index] = null;
			return;
		}

		int componentCount = texCoords.getComponentCount();

		if (componentCount < 2 || 3 < componentCount ||
			(this.fixed && texCoords.getVertexCount() != this.length) ||
			(bias != null && bias.length < componentCount))
			throw new java.lang.IllegalArgumentException();

		if (bias == null)
			bias = new float[componentCount];

		this.updateLength(texCoords.getVertexCount());
		this.texCoords[index] = texCoords;
		this.texCoordScale[index] = scale;
		this.texCoordBias[index] = bias;
	}

	private void updateLength(int length)
	{
		if (this.fixed) return;
		this.fixed = true;
		this.length = length;
	}

}
