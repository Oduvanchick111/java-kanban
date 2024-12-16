package com.yandex.kanban.service;

import com.yandex.kanban.model.Node;
import com.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> tail;
    private Node<Task> head;

    public void linkLast(Task task) {
        Node<Task> node = new Node<>(task);
        if (historyMap.isEmpty()) {
            tail = node;
            head = node;
            node.setNext(null);
            node.setPrev(null);
        } else {
            Node<Task> oldTail = tail;
            tail = node;
            node.setPrev(oldTail);
            node.setNext(null);
            node.getPrev().setNext(node);
        }
        historyMap.put(task.getId(), node);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> historyList = new ArrayList<>();
        if (historyMap.isEmpty()) {
            System.out.println("Здесь пусто");
        } else {
            for (Node<Task> node: historyMap.values()) {
                historyList.add(node.getData());
            }
        }
        return historyList;
    }

    public void removeNode (Node<Task> node) {
        if (node.getNext() == null) {
            Node<Task> prev = node.getPrev();
            tail = prev;
            prev.setNext(null);
        } else if (node.getPrev() == null){
            Node<Task> next = node.getNext();
            head = next;
            next.setPrev(null);
        } else {
            Node<Task> next = node.getNext();
            Node<Task> prev = node.getPrev();
            prev.setNext(next);
            next.setPrev(prev);
        }
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
        }
    }
}
