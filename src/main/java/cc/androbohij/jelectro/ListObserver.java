package cc.androbohij.jelectro;

import cc.androbohij.jelectro.shapes.ChargedObject;

public interface ListObserver {
    
    public void listAdd(ChargedObject chargedObject);
    public void listRemove(ChargedObject chargedObject);
    public void listClear();

}
