package com.pdfviewer.link;


import com.pdfviewer.model.LinkTapEvent;

public interface LinkHandler {

    void handleLinkEvent(LinkTapEvent event);
}
