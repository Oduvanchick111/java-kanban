package com.yandex.kanban.service;

import com.yandex.kanban.model.Node;
import com.yandex.kanban.model.Task;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> tail;
    private Node<Task> head;

    public void linkLast(Task task) {
        if (historyMap.containsKey(task.getId())){
            removeNode(historyMap.get(task.getId()));
        }
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
        Node<Task> currentNode = head;
        while (currentNode != null) {
            historyList.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return historyList;
    }

    public void removeNode(Node<Task> node) {
        Node<Task> prev = node.getPrev();
        Node <Task> next = node.getNext();
        if (node.getNext() == null) {
            tail = prev;
            if (prev != null) {
                prev.setNext(null);
            } else {
                head = null;
            }

        } else if (node.getPrev() == null) {
            head = next;
            next.setPrev(null);
        } else {
            prev.setNext(next);
            next.setPrev(prev);
        }
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
        }
        historyMap.remove(id);
    }
}
