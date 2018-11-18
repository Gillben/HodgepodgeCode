package com.gillben.hodgepodgecode.algorithm;

public class LinkMain {


    private static LinkNode reverseLinkList(LinkNode header) {
        if (header == null) {
            throw new RuntimeException("LinkNode's header is null");
        }
        if (header.getNext() == null) {
            return header;
        }
        LinkNode curNode = header;
        LinkNode preNode = null;
        LinkNode nextNode;

        while (curNode != null) {
            nextNode = curNode.getNext();
            curNode.setNext(preNode);
            preNode = curNode;
            curNode = nextNode;
        }
        return preNode;

    }


    private static LinkNode reverseLink2(LinkNode header){
        if (header == null || header.getNext() == null){
            return header;
        }
        LinkNode next = header.getNext();
        header.setNext(null);
        LinkNode reverseNode = reverseLink2(next);
        next.setNext(header);
        return reverseNode;
    }


    public static void main(String[] args) {
        LinkNode linkNode = new LinkNode(2);
        LinkNode linkNode1 = new LinkNode(3);
        LinkNode linkNode2 = new LinkNode(4);
        LinkNode linkNode3 = new LinkNode(5);
        LinkNode linkNode4 = new LinkNode(6);

        linkNode.setNext(linkNode1);
        linkNode1.setNext(linkNode2);
        linkNode2.setNext(linkNode3);
        linkNode3.setNext(linkNode4);

        LinkNode tempNode = reverseLink2(linkNode);
        while (tempNode != null){
            System.out.println(tempNode.getData());
            tempNode = tempNode.getNext();
        }
    }

}
