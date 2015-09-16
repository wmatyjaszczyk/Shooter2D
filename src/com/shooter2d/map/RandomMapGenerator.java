package com.shooter2d.map;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/*
 * TODO:
 * 	1. L-shaped hallways need improvement (don't work properly in two cases)
 * 	2. Checking if the separation of rooms don't push them outside the map (causes crashes)
 * 	3. Optimization
 * 
 */

public class RandomMapGenerator {
	private int[][] map;
	private Array<Room> rooms;
	private Array<Room> mainRooms;
	private Array<Room> hallways;
	private HashMap<Room, LinkedList<Room>> graph;
	private int width;
	private int height;

	private final int MAX_ROOM_WIDTH = 20;
	private final int MAX_ROOM_HEIGHT = 15;
	
	public static final int TILE_RED_ROOM = 1;
	public static final int TILE_BLUE_ROOM = 2;
	public static final int TILE_WALL = 3;
	public static final int TILE_HALLWAY = 4;

	public int[][] generateMap(int width, int height, int maxNumberOfRooms) {
		map = new int[width][height];
		rooms = new Array<Room>();
		mainRooms = new Array<Room>();
		hallways = new Array<Room>();
		graph = new HashMap<Room, LinkedList<Room>>();
		this.width = width;
		this.height = height;

		putRandomRoom(40, maxNumberOfRooms);
		separateRooms();
		selectMainRooms();
		connectMainRooms();
		createHallways();

		setTiles();
		drawToFile();

		return map;
	}

	private void putRandomRoom(float radius, int maxNumberOfRooms) {
		Random rand = new Random();
		
		// draw rooms at random positions in a circle
		for (int i = 0; i < maxNumberOfRooms; i++) {
			Vector2 point = getRandomPointInCircle(radius);

			// minimum width and height are 5
			Room room = new Room(Math.round(point.x + width / 2), Math.round(point.y + height / 2),
					5 + rand.nextInt(MAX_ROOM_WIDTH - 5), 5 + rand.nextInt(MAX_ROOM_WIDTH - 5));

			rooms.add(room);
		}
	}

	private void drawToFile() {
		// draw the map to a file
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Color c;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (map[i][j] == TILE_BLUE_ROOM)
					c = new Color(0, 0, 100);
				else if (map[i][j] == TILE_WALL)
					c = new Color(0, 100, 0);
				else if (map[i][j] == TILE_RED_ROOM)
					c = new Color(100, 0, 0);
				else if (map[i][j] == TILE_HALLWAY)
					c = new Color(70, 70, 190);
				else
					c = new Color(100, 100, 100);
				image.setRGB(i, j, c.getRGB());
			}
		}

		/*
		 * Graphics2D g = image.createGraphics(); g.setColor(new Color(0, 0,
		 * 0)); for (Map.Entry<Room, LinkedList<Room>> entry : graph.entrySet())
		 * { Room k = entry.getKey(); LinkedList<Room> v = entry.getValue();
		 * 
		 * for (Room tmp : v) { g.drawLine((int) k.getCenter().x, (int)
		 * k.getCenter().y, (int) tmp.getCenter().x, (int) tmp.getCenter().y); }
		 * }
		 */

		try {
			ImageIO.write(image, "bmp", new File("test.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setTiles() {
		// set tiles
		/*
		 * for (Room r : rooms) { for (int i = r.getX(); i < r.getX() +
		 * r.getWidth(); i++) { for (int j = r.getY(); j < r.getY() +
		 * r.getHeight(); j++) { map[i][j] = TILE_BLUE_ROOM;
		 * 
		 * if (i == r.getX() || i == r.getX() + r.getWidth() - 1) { map[i][j] =
		 * TILE_WALL; }
		 * 
		 * if (j == r.getY() || j == r.getY() + r.getHeight() - 1) { map[i][j] =
		 * TILE_WALL; } } } }
		 */

		// set tiles for main rooms
		for (Room r : mainRooms) {
			for (int i = r.getX(); i <= r.getX() + r.getWidth(); i++) {
				for (int j = r.getY(); j <= r.getY() + r.getHeight(); j++) {
					map[i][j] = TILE_RED_ROOM;
					
					if (i == r.getX() || i == r.getX() + r.getWidth()) {
						map[i][j] = TILE_WALL;
					}

					if (j == r.getY() || j == r.getY() + r.getHeight()) {
						map[i][j] = TILE_WALL;
					}
				}
			}
		}

		// set tiles for hallways
		for (Room r : hallways) {
			for (int i = r.getX(); i <= r.getX() + r.getWidth(); i++) {
				for (int j = r.getY(); j <= r.getY() + r.getHeight(); j++) {
					map[i][j] = TILE_HALLWAY;
				}
			}
		}

		// surround hallways with walls
		for (Room r : hallways) {
			for (int i = r.getX(); i <= r.getX() + r.getWidth(); i++) {
				for (int j = r.getY(); j <= r.getY() + r.getHeight(); j++) {
					if (i == r.getX() + r.getWidth() && map[i + 1][j] == 0) {
						map[i][j] = TILE_WALL;
					}

					if (i == r.getX() && map[i - 1][j] == 0) {
						map[i][j] = TILE_WALL;
					}

					if (j == r.getY() + r.getHeight() && map[i][j + 1] == 0) {
						map[i][j] = TILE_WALL;
					}

					if (j == r.getY() && map[i][j - 1] == 0) {
						map[i][j] = TILE_WALL;
					}
				}
			}
		}

		// remove unnecessary walls
		for (Room r : mainRooms) {
			for (int i = r.getX(); i <= r.getX() + r.getWidth(); i++) {
				for (int j = r.getY(); j <= r.getY() + r.getHeight(); j++) {
					if (map[i + 1][j] == TILE_WALL && map[i + 2][j] == TILE_RED_ROOM) {
						map[i + 1][j] = TILE_HALLWAY;
					}

					if (map[i - 1][j] == TILE_WALL && map[i - 2][j] == TILE_RED_ROOM) {
						map[i - 1][j] = TILE_HALLWAY;
					}

					if (map[i][j + 1] == TILE_WALL && map[i][j + 2] == TILE_RED_ROOM) {
						map[i][j + 1] = TILE_HALLWAY;
					}

					if (map[i][j - 1] == TILE_WALL && map[i][j - 2] == TILE_RED_ROOM) {
						map[i][j - 1] = TILE_HALLWAY;
					}
				}
			}
		}
	}

	private Vector2 getRandomPointInCircle(float radius) {
		Random rand = new Random();

		float tmp = (float) (2 * Math.PI * rand.nextFloat());
		float tmp2 = rand.nextFloat() + rand.nextFloat();
		float r;

		if (tmp2 > 1) {
			r = 2 - tmp2;
		} else {
			r = tmp2;
		}

		return new Vector2((float) (r * radius * Math.cos(tmp)), (float) (r * radius * Math.sin(tmp)));
	}

	private void separateRooms() {
		Room roomA, roomB;
		int dx, dxA, dxB, dy, dyA, dyB;
		boolean isTouching;

		do {
			isTouching = false;

			for (int i = 0; i < rooms.size; i++) {
				roomA = rooms.get(i);

				for (int j = i + 1; j < rooms.size; j++) {
					roomB = rooms.get(j);

					if (roomA.intersect(roomB)) {
						isTouching = true;

						dx = Math.min((roomA.getX() + roomA.getWidth()) - roomB.getX(),
								(roomB.getX() + roomB.getWidth()) - roomA.getX());
						dy = Math.min((roomA.getY() + roomA.getHeight()) - roomB.getY(),
								(roomB.getY() + roomB.getHeight()) - roomA.getY());

						if (Math.abs(dx) < Math.abs(dy)) {
							dy = 0;
						} else {
							dx = 0;
						}

						dxA = -dx / 2;
						dxB = dx + dxA;
						dyA = -dy / 2;
						dyB = dy + dyA;

						roomA.move(dxA, dyA);
						roomB.move(dxB, dyB);
					}
				}
			}
		} while (isTouching);
	}

	private void selectMainRooms() {
		int tWidth = (int) (MAX_ROOM_WIDTH / 2 * 1.25);
		int tHeight = (int) (MAX_ROOM_HEIGHT / 2 * 1.25);

		for (Room r : rooms) {
			if (r.getWidth() >= tWidth && r.getHeight() >= tHeight) {
				mainRooms.add(r);
				rooms.removeValue(r, true);
			}
		}
	}

	private void connectMainRooms() {
		Room roomA, roomB, roomC;
		double distAB, distBC, distAC;
		boolean smallestDist;

		// this function is creating relative neighborhood graph so that every
		// main room is connected
		// to each other (directly or indirectly) and there are some loops

		// checking distance for every pair of rooms
		for (int i = 0; i < mainRooms.size; i++) {
			roomA = mainRooms.get(i);

			for (int j = i + 1; j < mainRooms.size; j++) {
				roomB = mainRooms.get(j);

				distAB = Point2D.distanceSq(roomA.getCenter().x, roomA.getCenter().y, roomB.getCenter().x,
						roomB.getCenter().y);

				smallestDist = true;

				// checking all other rooms
				for (int k = 0; k < mainRooms.size; k++) {
					if (k != i && k != j) {
						roomC = mainRooms.get(k);

						distBC = Point2D.distanceSq(roomB.getCenter().x, roomB.getCenter().y, roomC.getCenter().x,
								roomC.getCenter().y);

						distAC = Point2D.distanceSq(roomA.getCenter().x, roomA.getCenter().y, roomC.getCenter().x,
								roomC.getCenter().y);

						// if distAB is not the smallest possible between those
						// two points it is not the edge
						if (distBC < distAB && distAC < distAB) {
							smallestDist = false;
							break;
						}
					}
				}

				// if distAB is the smallest distance add it to the graph
				if (smallestDist) {
					if (graph.get(roomA) == null) {
						graph.put(roomA, new LinkedList<Room>());
					}

					graph.get(roomA).add(roomB);
				}
			}
		}
	}

	private void createHallways() {
		int midPointX, midPointY, hX, hY, hWidth, hHeight;

		// create hallways between rooms according to the graph
		for (Map.Entry<Room, LinkedList<Room>> entry : graph.entrySet()) {
			Room k = entry.getKey();
			LinkedList<Room> v = entry.getValue();

			for (Room tmp : v) {
				midPointX = (int) (k.getCenter().x + tmp.getCenter().x) / 2;
				midPointY = (int) (k.getCenter().y + tmp.getCenter().y) / 2;

				// check whether one of the midpoints is in both rooms
				if (midPointX >= k.getX() && midPointX <= k.getX() + k.getWidth()) {
					if (midPointX > tmp.getX() && midPointX < tmp.getX() + tmp.getWidth()) {
						hX = midPointX;
						hY = Math.min(k.getY() + k.getHeight(), tmp.getY() + tmp.getHeight());
						hHeight = Math.abs(Math.min(k.getY() + k.getHeight() - tmp.getY(),
								tmp.getY() + tmp.getHeight() - k.getY()));
						hWidth = 4;

						Room hallway = new Room(hX, hY, hWidth, hHeight);
						hallways.add(hallway);
					}
				} else if (midPointY >= k.getY() && midPointY <= k.getY() + k.getHeight()) {
					if (midPointY > tmp.getY() && midPointY < tmp.getY() + tmp.getHeight()) {
						hX = Math.min(k.getX() + k.getWidth(), tmp.getX() + tmp.getWidth());
						hY = midPointY;
						hWidth = Math.abs(
								Math.min(k.getX() + k.getWidth() - tmp.getX(), tmp.getX() + tmp.getWidth() - k.getX()));
						hHeight = 4;

						Room hallway = new Room(hX, hY, hWidth, hHeight);
						hallways.add(hallway);
					}
				} else {
					// if none of the midpoints is in both rooms create an
					// L-shaped hallway
					Room roomA, roomB, hallway, hallway2;
					int hDX, hDY;

					// roomA has to be to the left
					if (k.getCenter().x < tmp.getCenter().x) {
						roomA = k;
						roomB = tmp;
					} else {
						roomA = tmp;
						roomB = k;
					}

					hX = (int) roomA.getCenter().x + roomA.getWidth() / 2;
					hY = (int) roomA.getCenter().y;
					hDX = (int) (roomB.getCenter().x - hX);

					hallway = new Room(hX, hY, hDX, 4);
					hallways.add(hallway);

					// roomA has to be to the top
					if (k.getCenter().y < tmp.getCenter().y) {
						hDY = (int) (roomB.getCenter().y - (roomB.getHeight() / 2) - hY);
						hallway2 = new Room(hX + hDX, hY, 4, hDY);

						System.out.println("1: " + hDY);
					} else {
						roomA = tmp;
						roomB = k;

						hX = (int) roomA.getCenter().x;
						hY = (int) roomA.getCenter().y + (roomA.getHeight() / 2);
						hDY = (int) Math.abs((roomB.getCenter().y - hY));
						hallway2 = new Room(hX, hY, 4, hDY + 4);

						System.out.println("2: " + hDY);
					}

					hallways.add(hallway2);
				}
			}
		}

		// add intersected rooms
		addRoomsToHallways();
	}

	private void addRoomsToHallways() {
		// if a hallway intersects a room which is not a main room then add it
		// to the hallways
		Array<Room> newHallways = new Array<Room>();

		for (Room h : hallways) {
			for (Room r : rooms) {
				if (h.intersect(r)) {
					newHallways.add(r);
					rooms.removeValue(r, true);
				}
			}
		}

		for (Room h : newHallways) {
			hallways.add(h);
		}
	}
}
