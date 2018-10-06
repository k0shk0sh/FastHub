package com.jaychang.srv;

import java.util.List;

interface CellOperations {

  void addCell(SimpleCell cell);

  void addCell(int atPosition, SimpleCell cell);

  void addCells(List<? extends SimpleCell> cells);

  void addCells(SimpleCell... cells);

  void addCells(int fromPosition, List<? extends SimpleCell> cells);

  void addCells(int fromPosition, SimpleCell... cells);

  <T extends SimpleCell & Updatable> void addOrUpdateCell(T cell);

  <T extends SimpleCell & Updatable> void addOrUpdateCells(List<T> cells);

  <T extends SimpleCell & Updatable> void addOrUpdateCells(T... cells);

  void removeCell(SimpleCell cell);

  void removeCell(int atPosition);

  void removeCells(int fromPosition, int toPosition);

  void removeCells(int fromPosition);

  void removeAllCells();

  void updateCell(int atPosition, Object payload);

  void updateCells(int fromPosition, int toPosition, Object payloads);

  SimpleCell getCell(int atPosition);

  List<SimpleCell> getCells(int fromPosition, int toPosition);

  List<SimpleCell> getAllCells();

}
