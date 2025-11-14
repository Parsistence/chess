package server;

import model.GameData;

import java.util.Collection;

public record GameDataList(Collection<GameData> games) {
}
