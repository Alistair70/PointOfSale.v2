package pos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;




public class Pos {

    
    public static SerialPort comPort;
    
    public static void main(String[] args) throws IOException 
    {           
        createUI();
    }
    
    public static void createUI()
    {        
        LinkedList<String> cats = new LinkedList<String>();
        LinkedList<String> prices = new LinkedList<String>();
        
        String initQuery = "insert into salesRecord (dateOfTrans) values (current_date())";
        inventoryConnection.executeQuery(initQuery);
        
        JFrame frame1 = new JFrame("Point of Sale");
                
        JMenuBar menu = new JMenuBar();
        JMenuItem addItem = new JMenuItem("Add Item");
        JMenuItem editItem = new JMenuItem("Edit Item");
        JMenuItem addCat = new JMenuItem("Add New Categeory");
                
        JMenu optionsMenu = new JMenu("Options");
       
        optionsMenu.add(addItem);
        optionsMenu.add(editItem);
        optionsMenu.add(addCat);
        menu.add(optionsMenu);
        
        JPanel checkout = new JPanel();
        JPanel superCheckout = new JPanel();
        
        JPanel optionsPanel = new JPanel();        
        JPanel total = new JPanel();
        JPanel options = new JPanel();
            
        options.setLayout(new GridLayout(0, 2));
        
        JLabel totalLabel = new JLabel("Total:");
        JLabel totalAmt = new JLabel("0");
        JButton openReg = new JButton("Open Register");
        JButton reset = new JButton("Reset Trans.");
        JButton refund = new JButton("Refund");
        JButton cashOut = new JButton("Cash Out");
        JButton multi = new JButton("Multi-Entry");
        JButton manual = new JButton("Manual Entry");
        
        total.add(totalLabel);
        total.add(totalAmt);
        options.add(openReg);
        options.add(reset);
        options.add(multi); 
        options.add(manual);
        options.add(refund);
        options.add(cashOut);

        
        optionsPanel.add(BorderLayout.NORTH, total);
        optionsPanel.add(BorderLayout.SOUTH, options);        

        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(1000, 750);   
      
             
        optionsPanel.setLayout(new GridLayout(0, 1));
        
        checkout.setLayout(new GridLayout(0, 5));
        checkout.add(new JLabel("ID"));
        checkout.add(new JLabel("Name"));
        checkout.add(new JLabel("Category"));
        checkout.add(new JLabel("Quantity"));
        checkout.add(new JLabel("Price($)"));
        
        superCheckout.add(BorderLayout.NORTH, checkout);

        frame1.add(BorderLayout.NORTH, menu);
        frame1.add(BorderLayout.CENTER, new JScrollPane(superCheckout));
        frame1.add(BorderLayout.EAST, optionsPanel);
        frame1.setVisible(true);
        frame1.setFocusable(true);
        
        openReg.setFocusable(false);
        reset.setFocusable(false);
        cashOut.setFocusable(false);
        manual.setFocusable(false);
        refund.setFocusable(false);
        multi.setFocusable(false);
        
        
         frame1.addKeyListener(new KeyListener(){
            String bcode = "";
            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    {
                        String[] prod = inventoryConnection.getProd(bcode);
                        int toAdd = 0;
                                    
                        
                        if(prod[0] != null)
                        {
                            checkout.add(new JLabel(prod[0]));
                            checkout.add(new JLabel(prod[1]));
                            checkout.add(new JLabel(prod[2]));
                            checkout.add(new JLabel("1"));
                            checkout.add(new JLabel(prod[3]));
                            
                            cats.add(prod[2]);
                            prices.add(prod[3]);
                            
                            checkout.revalidate();
                            
                            toAdd = Integer.valueOf(prod[3]);
                        }
                        
                        int prevTotal = Integer.valueOf(totalAmt.getText());                        
                        int sum = prevTotal + toAdd;

                        totalAmt.setText(String.valueOf(sum));
                        
                        bcode = "";                        
                    }
                else
                    bcode += e.getKeyChar();  
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
            
        });        
        
        addItem.addActionListener( new ActionListener() // Add Function
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                
                JFrame addFrame = new JFrame("Add Item");
                addFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                addFrame.setSize(400, 300);
                addFrame.setVisible(true);
                
                JLabel barcodeInLabel = new JLabel("Barcode ID:");
                JLabel itemNameLabel = new JLabel("Item Name:"); 
                JLabel itemCat = new JLabel("Item Category:");
                JLabel priceLabel = new JLabel("Price: ");  
                
                JTextField barcodeIn = new JTextField();
                JTextField itemNameIn = new JTextField();
                JComboBox category = new JComboBox(inventoryConnection.getCols());
                JTextField priceIn = new JTextField();      
               
                priceIn.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if ( ((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                            e.consume();  
                        }
                     }
                });
                
                JButton confirm = new JButton("Add Item");
                                                
                JPanel infoPanel = new JPanel();
                JPanel executable = new JPanel();
                
                infoPanel.setLayout(new GridLayout(0, 2));
                
                infoPanel.add(barcodeInLabel);
                infoPanel.add(barcodeIn);
                infoPanel.add(itemNameLabel);
                infoPanel.add(itemNameIn);
                infoPanel.add(itemCat);
                infoPanel.add(category);
                infoPanel.add(priceLabel);                
                infoPanel.add(priceIn);                
                executable.add(confirm);
                
                addFrame.add(BorderLayout.NORTH, infoPanel);
                addFrame.add(BorderLayout.SOUTH, executable);
                
                barcodeIn.setFocusable(true);
                
                barcodeIn.addKeyListener(new KeyListener(){
                    String bcode = "";
                    @Override
                    public void keyTyped(KeyEvent e) {
                        
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if(e.getKeyCode() == KeyEvent.VK_ENTER)
                            {
                                barcodeIn.setText(bcode);
                                bcode = "";
                            }
                        else
                        {
                            bcode += e.getKeyChar();
                            System.out.println(bcode);
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        
                    }
                });        
                
                confirm.addActionListener( new ActionListener() 
                {
                @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                    String    w = barcodeIn.getText();
                    String    x = itemNameIn.getText();
                    String    y = category.getSelectedItem().toString();
                    String    z = priceIn.getText();
                    String query = "Insert into productInventory values ('"+w+"','"+x+"','"+y+"',"+z+");";
                    
                    inventoryConnection.executeQuery(query);
                    barcodeIn.setText("");
                    itemNameIn.setText("");
                    priceIn.setText("");
                    
                    barcodeIn.requestFocus();                    
                    }
                });       
            }
        }); 
        
        editItem.addActionListener( new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                               
                JFrame editFrame = new JFrame("Edit Item");
                editFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                editFrame.setSize(500, 300);
                editFrame.setVisible(true);
                
                JLabel blank = new JLabel("");
                JLabel currInfo = new JLabel("Current Values");
                JLabel newValues = new JLabel("New Values:");
                JLabel n1 = new JLabel("Name:");
                JLabel p1 = new JLabel("Price:");
                JLabel c1 = new JLabel("Category:");
                
                JPanel barcodePanel = new JPanel();
                JPanel infoPanel = new JPanel();
                JPanel executable = new JPanel();
                
                JLabel currName = new JLabel("");
                JTextField newName = new JTextField();
                JLabel currCat = new JLabel("");
                JComboBox newCat = new JComboBox(inventoryConnection.getCols());
                JLabel currPrice = new JLabel("");
                JTextField newPrice = new JTextField("");
                JTextField barcodeIn = new JTextField("");
                
                JButton confirm = new JButton("Confirm"); 
                JButton rescan = new JButton("Re-scan");
                
                newPrice.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if ( ((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                            e.consume();  
                        }
                     }
                });
               
                infoPanel.setLayout(new GridLayout(0, 3));
                barcodePanel.setLayout(new GridLayout(0,1));
                executable.setLayout(new GridLayout(0,2));                
                
                infoPanel.add(blank);
                infoPanel.add(currInfo);
                infoPanel.add(newValues);
                
                infoPanel.add(n1);
                infoPanel.add(currName);
                infoPanel.add(newName);
               
                infoPanel.add(c1);
                infoPanel.add(currCat);
                infoPanel.add(newCat);
                
                infoPanel.add(p1);
                infoPanel.add(currPrice);
                infoPanel.add(newPrice);
                        
                
                executable.add(confirm);
                executable.add(rescan);
                
                editFrame.add(BorderLayout.NORTH, barcodePanel);
                editFrame.add(BorderLayout.CENTER, infoPanel);
                editFrame.add(BorderLayout.SOUTH, executable);
                
                editFrame.requestFocus();                             
                
                editFrame.addKeyListener(new KeyListener()
                {
                    String bcode = "";
                    @Override
                    public void keyTyped(KeyEvent e) {
                        
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if(e.getKeyCode() == KeyEvent.VK_ENTER)
                            {
                                String[] prod = inventoryConnection.getProd(bcode);

                                if(prod[0] != null)
                                {
                                    barcodeIn.setText(prod[0]);
                                    currName.setText(prod[1]);
                                    currCat.setText(prod[2]);
                                    currPrice.setText(prod[3]);
                                }
                                bcode = "";                        
                            }
                        else
                            bcode += e.getKeyChar();  
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        
                    }
                });                 
                
                rescan.addActionListener( new ActionListener() 
                {
                @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                        editFrame.requestFocus();
                    }
                });                
                
                confirm.addActionListener( new ActionListener() 
                {
                @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                        String newNameIn = newName.getText();
                        String newPriceIn = newPrice.getText();
                        String newCatIn = newCat.getSelectedItem().toString();
                        String barcodeInn = barcodeIn.getText();
                        
                        System.out.println(newNameIn) ;
                        
                        if(!newNameIn.equals(""))
                        {
                            inventoryConnection.executeQuery("UPDATE productInventory set "
                                    + "product_Name = '"+newNameIn+"' where product_id = "+barcodeInn);
                        }    
                        if(!newCatIn.equals(""))
                        {
                            inventoryConnection.executeQuery("UPDATE productInventory set"
                                    + " product_categeory = '"+newCatIn+"' where product_id = "+barcodeInn);
                        }
                        if(!newPriceIn.equals(""))
                        {
                            inventoryConnection.executeQuery("UPDATE productInventory set"
                                    + " product_Price = '"+newPriceIn+"' where product_id = "+barcodeInn);
                        }
                        
                    
                    //editFrame.hide();
                    editFrame.dispose();
                    }
                }); 
                
                
            }
        });
        
        addCat.addActionListener( new ActionListener() 
            {
            @Override
                 public void actionPerformed(ActionEvent e) 
                {
                    JFrame addCatFrame = new JFrame("Add New Categeory");
                    addCatFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    addCatFrame.setSize(300, 200);
                    addCatFrame.setVisible(true);
                    
                    JLabel newCatInLabel = new JLabel("Enter New Categeory:");
                    JTextField newCatIn = new JTextField();
                    JButton confirmNewCat = new JButton("Comfirm");
                    JPanel newCat = new JPanel();                     
                    
                    JPanel catInPanel = new JPanel();
                    JPanel confirmPan = new JPanel();
                    
                    newCat.setLayout(new GridLayout(0,1));
                    catInPanel.setLayout(new GridLayout(0,2));
                    
                    catInPanel.add(newCatInLabel);
                    catInPanel.add(newCatIn);
                    confirmPan.add(confirmNewCat);
                            
                    addCatFrame.add(BorderLayout.NORTH, catInPanel);
                    addCatFrame.add(BorderLayout.SOUTH, confirmPan);
                    
                    confirmNewCat.addActionListener( new ActionListener() 
                        {
                            @Override
                            public void actionPerformed(ActionEvent e) 
                            {
                                JFrame confirmCatFrame = new JFrame("Confirm New Categeory");
                                confirmCatFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                                confirmCatFrame.setSize(300, 200);
                                confirmCatFrame.setVisible(true);
                                
                                String newCat = newCatIn.getText();
                                 
                                JButton confirm2  = new JButton("Confirm");
                                JLabel conText = new JLabel("Confirm New Categeory");
                                JLabel catToBe = new JLabel(newCat);
                                
                                JPanel confirm2Panel = new JPanel(); 
                                confirm2Panel.setLayout(new GridLayout(0,1));
                                
                                JPanel execute = new JPanel();
                                
                                confirm2Panel.add(conText);
                                confirm2Panel.add(catToBe);
                                execute.add(confirm2);
                                confirmCatFrame.add(BorderLayout.NORTH, confirm2Panel);
                                confirmCatFrame.add(BorderLayout.SOUTH, execute);
                                
                                confirm2.addActionListener( new ActionListener() 
                                {
                                @Override
                                     public void actionPerformed(ActionEvent e) 
                                    {
                                        String query = "ALTER table salesRecord ADD "+ newCat +" int DEFAULT 0";
                                        inventoryConnection.executeQuery(query);
                                    }
                                });                                
                            }
                        });
                }
            });
        
        
        
       
        reset.addActionListener( new ActionListener() 
            {
            @Override
                 public void actionPerformed(ActionEvent e) 
                {
                    checkout.removeAll();
                    checkout.revalidate();
                                
                    checkout.add(new JLabel("ID"));
                    checkout.add(new JLabel("Name"));
                    checkout.add(new JLabel("Category"));
                    checkout.add(new JLabel("Quantity"));
                    checkout.add(new JLabel("Price($)"));
                    
                    totalAmt.setText("0");

                    cats.clear();
                    prices.clear();
                }
            });        
        
        refund.addActionListener( new ActionListener() 
                {
                @Override
                    public void actionPerformed(ActionEvent e) 
                    {                            
                        JFrame refundFrame = new JFrame("Refund");
                        refundFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        refundFrame.setSize(400, 150);
                        refundFrame.setVisible(true); 
                        
                        
                        JPanel refundPanel = new JPanel();
                        refundPanel.setLayout(new GridLayout(0, 5));
                        
                        JLabel t1 = new JLabel("ID");
                        JLabel t2 = new JLabel("Name");
                        JLabel t3 = new JLabel("Category");
                        JLabel t4 = new JLabel("Quantity");
                        JLabel t5 = new JLabel("Price($)");
                        
                        JLabel refundedId = new JLabel();
                        JLabel refundedName = new JLabel();
                        JLabel refundedCat = new JLabel();
                        JLabel refundedQuantity = new JLabel();
                        JLabel refundedPrice = new JLabel();
                        
                        refundPanel.add(t1);
                        refundPanel.add(t2);
                        refundPanel.add(t3);
                        refundPanel.add(t4);
                        refundPanel.add(t5);
                        
                        refundPanel.add(refundedId);
                        refundPanel.add(refundedName);
                        refundPanel.add(refundedCat);
                        refundPanel.add(refundedQuantity);
                        refundPanel.add(refundedPrice);

                        refundFrame.add(refundPanel);

                        refundFrame.addKeyListener(new KeyListener(){
                            
                            String barcode = "";
                            
                            @Override
                            public void keyTyped(KeyEvent e) {
                            }
                            @Override
                            public void keyPressed(KeyEvent e) {              

                               if(e.getKeyCode() == KeyEvent.VK_ENTER)
                               {
                                   String[] prod = inventoryConnection.getProd(barcode);
                            
                                    checkout.add(new JLabel(prod[0])).setForeground(Color.red);
                                    checkout.add(new JLabel(prod[1])).setForeground(Color.red);
                                    checkout.add(new JLabel(prod[2])).setForeground(Color.red);
                                    checkout.add(new JLabel("-1")).setForeground(Color.red);                                    
                                    checkout.add(new JLabel("-" + prod[3])).setForeground(Color.red);
                                    
                                    refundedId.setText(prod[0]);
                                    refundedName.setText(prod[1]);
                                    refundedCat.setText(prod[2]);
                                    refundedQuantity.setText("-1");
                                    refundedPrice.setText("-" + prod[3]);
                                    
                                    cats.add(prod[2]);
                                    prices.add("-" + prod[3]);                                    
                                    
                                    checkout.revalidate();

                                    int prevTotal = Integer.valueOf(totalAmt.getText());
                                    int toAdd = Integer.valueOf(prod[3]);
                                    int sum = prevTotal - toAdd;

                                    totalAmt.setText(String.valueOf(sum));

                                    //frame1.revalidate();
                                    barcode = "";
                               }
                               else
                                   barcode += e.getKeyChar();
                            }
                            @Override
                            public void keyReleased(KeyEvent e) {
                               
                            }
                        });
                    }
                });
        
        multi.addActionListener( new ActionListener() 
                {
                @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                            
                        JFrame multiFrame = new JFrame("Multi-Entry");
                        multiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        multiFrame.setSize(300, 100);
                        multiFrame.setVisible(true); 
                        JLabel amountLabel = new JLabel("Amount:");
                        JTextField amountField = new JTextField();
                        JButton amountConfirm = new JButton("Confirm");
                        
                        multiFrame.addKeyListener(new KeyListener(){
                            
                            String barcode = "";
                            
                            
                            @Override
                            public void keyTyped(KeyEvent e) {
                            }
                            @Override
                            public void keyPressed(KeyEvent e) {              

                               if(e.getKeyCode() == KeyEvent.VK_ENTER)
                               {
                                   
                                   JFrame amount = new JFrame("Enter Amount");
                                   amount.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                   amount.setSize(300, 100);
                                   amount.setLayout(new GridLayout(0,1));
                                   amount.setVisible(true);
                                   amount.add(amountLabel);
                                   amount.add(amountField);
                                   amount.add(amountConfirm);
                                   
                                   amountField.addKeyListener(new KeyAdapter() {
                                        public void keyTyped(KeyEvent e) {
                                            char c = e.getKeyChar();
                                            if ( ((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                                                e.consume();  
                                            }
                                         }
                                    });
                                   
                                   amountConfirm.addActionListener( new ActionListener() 
                                    {
                                    @Override
                                        public void actionPerformed(ActionEvent e) 
                                        {
                                            String quantity = amountField.getText();
                                            String[] prod = inventoryConnection.getProd(barcode);
                                            
                                            int fullCost = Integer.valueOf(quantity) * Integer.valueOf(prod[3]);
                                            String fullCostString = String.valueOf(fullCost);
                                            
                                            checkout.add(new JLabel(prod[0])).setForeground(Color.GREEN);
                                            checkout.add(new JLabel(prod[1])).setForeground(Color.green);
                                            checkout.add(new JLabel(prod[2])).setForeground(Color.green);
                                            checkout.add(new JLabel(quantity)).setForeground(Color.green);
                                            checkout.add(new JLabel(fullCostString)).setForeground(Color.green);
                                            checkout.revalidate();
                                            amount.dispose();
                                            multiFrame.dispose();
                                            
                                            cats.add(prod[2]);
                                            prices.add(String.valueOf(fullCost));
                                            
                                            int prevTotal = Integer.valueOf(totalAmt.getText());
                                            int sum = prevTotal + fullCost;
                                            totalAmt.setText(String.valueOf(sum));
                                            
                                        }
                                    }); 

                               }
                               else
                                   barcode += e.getKeyChar();
                            }
                            @Override
                            public void keyReleased(KeyEvent e) {
                               
                            }
                        });
                        
                        

   
                    }
                });
        
        manual.addActionListener( new ActionListener() 
        {
        @Override
            public void actionPerformed(ActionEvent e) 
            {
                JFrame manualFrame = new JFrame("Manual Entry");
                manualFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                manualFrame.setSize(400, 150);
                manualFrame.setVisible(true);

                JPanel manualPanel = new JPanel();
                manualPanel.setLayout(new GridLayout(0,2));

                JLabel GoodsType = new JLabel("Categoery:");
                JComboBox archs = new JComboBox(inventoryConnection.getCols());
                JLabel priceLabel = new JLabel("Cost($):");
                JTextField amountIn = new JTextField();
                JButton add = new JButton("Add Item");


                manualPanel.add(GoodsType);
                manualPanel.add(archs);
                manualPanel.add(priceLabel);
                manualPanel.add(amountIn);

                amountIn.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if ( ((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                            e.consume();  
                        }
                     }
                });

                manualFrame.add(BorderLayout.CENTER, manualPanel);
                manualFrame.add(BorderLayout.SOUTH, add);   

                add.addActionListener( new ActionListener() 
                {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                        String type = archs.getSelectedItem().toString();
                        String amtIn = amountIn.getText(); 

                        checkout.add(new JLabel(""));
                        checkout.add(new JLabel(type));
                        checkout.add(new JLabel(type));
                        checkout.add(new JLabel(""));
                        checkout.add(new JLabel(amtIn));

                        checkout.revalidate();

                        cats.add(type);
                        prices.add(amtIn);

                        int prevTotal = Integer.valueOf(totalAmt.getText());
                        int toAdd = Integer.valueOf(amtIn);
                        int sum = prevTotal + toAdd;
                        totalAmt.setText(String.valueOf(sum));

                        amountIn.setText("");
                        archs.requestFocus();

                    }
                }); 
            }
        });
        
        cashOut.addActionListener( new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                JFrame cashOutFrame = new JFrame("Cash Out");
                cashOutFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                cashOutFrame.setSize(400, 300);
                cashOutFrame.setVisible(true);
                        
                JPanel cashOutPanel = new JPanel();
                JPanel cashOutOptionsPanel = new JPanel();
                cashOutPanel.setLayout(new GridLayout(0,2));
                        
                JLabel finalTotalLabel = new JLabel("Final Total");
                JLabel finalTotal = new JLabel(totalAmt.getText());
                JLabel cashInLabel = new JLabel("Cash Tendered:");
                JTextField cashIn = new JTextField("");
                JLabel changeLabel = new JLabel("Change:");
                JLabel change = new JLabel("");
                        
                JButton cashOutFinal = new JButton("Cash Out");
                JButton finished = new JButton("Finish Transaction");

                cashOutPanel.add(finalTotalLabel);
                cashOutPanel.add(finalTotal);
                cashOutPanel.add(cashInLabel);
                cashOutPanel.add(cashIn);
                cashOutPanel.add(changeLabel);
                cashOutPanel.add(change);

                cashOutOptionsPanel.add(cashOutFinal);
                cashOutOptionsPanel.add(finished);

                finished.setVisible(false);

                cashOutFrame.add(BorderLayout.NORTH, cashOutPanel);
                cashOutFrame.add(BorderLayout.SOUTH, cashOutOptionsPanel);

                System.out.println(cats);
                System.out.println(prices);

                cashOutFinal.addActionListener( new ActionListener() 
                {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                        int fullTotal = Integer.valueOf(totalAmt.getText());
                        int cashTendered = Integer.valueOf(cashIn.getText());
                        int changeOut = cashTendered - fullTotal;

                        change.setText(String.valueOf(changeOut));

                        for(int i = 0; i < cats.size(); i++)
                        {
                            String query = "update salesRecord set "+ cats.get(i) +" = "+ cats.get(i) +" + "+prices.get(i)+" where dateOfTrans = CURDATE()";
                            inventoryConnection.executeQuery(query);
                        }
                        cats.clear();
                        prices.clear();
                        finished.setVisible(true);
                    }
                });

                finished.addActionListener( new ActionListener() 
                {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                        checkout.removeAll();
                        checkout.revalidate();
                        openSesame();

                        checkout.add(new JLabel("ID"));
                        checkout.add(new JLabel("Name"));
                        checkout.add(new JLabel("Category"));
                        checkout.add(new JLabel("Quantity"));
                        checkout.add(new JLabel("Price($)"));

                        cashOutFrame.dispose();

                        totalAmt.setText("0");
                    }
                });
            }
        });
       
        openReg.addActionListener( new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {                                   
                openSesame();
            }
        });
        
    } 
    
    public static void openSesame()
    {
        SerialPort[] ports = SerialPort.getCommPorts();
        comPort = ports[0];
        comPort.openPort();
        OutputStream out = comPort.getOutputStream();
        String open = "open";
        byte[] openByte = open.getBytes();
        try 
        {
            out.write(openByte);
        } catch (IOException ex) {Logger.getLogger(Pos.class.getName()).log(Level.SEVERE, null, ex);}
        comPort.closePort();
    }
}

