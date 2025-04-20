public class Subtask extends Task {
    private int epicId; // Достаточно id эпика для ссылки на него

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
