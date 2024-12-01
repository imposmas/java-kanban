package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    HashMap<Integer, Node> tasksHistory = new HashMap<>();
    Node first;
    Node last;


    @Override
    public List<Task> getHistory() {
        ArrayList<Task> historyList = new ArrayList<>();
        Node current = first;
        while (current != null) {
            historyList.add(current.item);
            current = current.next;
        }
        return historyList;
    }

    @Override
    public void addToHistory(Task task){
        Node node = tasksHistory.get(task.getId());
        if(node != null) {
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if(tasksHistory.get(id) != null) {
            Node taskToNode = tasksHistory.get(id);
            removeNode(taskToNode);
        }
    }

    void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        tasksHistory.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        Node before = node.prev;
        Node after = node.next;
        if(before == null){
            first = after;
        } else if(after == null){
            last = before;
        } else {
            before.next = after;
            after.prev = before;
        }
        tasksHistory.remove(node.item.getId());
    }

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.item = task;
            this.next = next;
            this.prev = prev;
        }
    }

}
