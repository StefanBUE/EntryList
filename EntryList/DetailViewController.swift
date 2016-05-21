//
//  DetailViewController.swift
//  MathMonsters
//
//  Created by Stefan Buehlmann on 12.05.16.
//  Copyright © 2016 Stefan Buehlmann. All rights reserved.
//

import UIKit

class DetailViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var idLabel: UILabel!
    @IBOutlet weak var nameField: UITextField!
    @IBOutlet weak var descriptionField: UITextField!
    
    var entryVM: EntryViewModel? {
        didSet (newEntry) {
            self.refreshUI()
        }
    }
    
    required init?(coder decoder: NSCoder) {
        // do I have to persist some information about EntryServiceI or PersistenceI ?
        super.init(coder: decoder)
    }
    
    private func refreshUI() {
        if entryVM != nil {
            idLabel?.text = "\(entryVM!.getId())"
            nameField?.text = entryVM!.getName()
            descriptionField?.text = entryVM!.getDescription()
        } else {
            idLabel?.text = ""
            nameField?.text = ""
            descriptionField?.text = ""
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        self.nameField.delegate=self;
        self.descriptionField.delegate=self;
        refreshUI()
    }

    override func viewWillDisappear(animated:Bool) {
        // This method will need to change if there is another view to forward to in addition to the
        // view to go back to.
        if entryVM != nil {
            // this works on iPhone, because the view will really disappear, but not on iPAD !
            // for iPad the UITextFieldDelegate (see below) was implemented
            entryVM!.saveWithNSString(self.nameField.text, withNSString: self.descriptionField.text)
        }
        self.nameField.delegate=nil;
        self.descriptionField.delegate=nil;
        super.viewWillDisappear(animated)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    
    /************** interface UITextFieldDelegate **************/
    var fieldUnderEdit: UITextField?
    func textFieldDidBeginEditing(textField: UITextField) {
        fieldUnderEdit = textField
        NSNotificationCenter.defaultCenter().addObserver(self, selector: #selector(DetailViewController.textFieldModified), name: UITextFieldTextDidChangeNotification, object: textField)
    }
    
    func textFieldDidEndEditing(textField: UITextField) {
        fieldUnderEdit = nil
        NSNotificationCenter.defaultCenter().removeObserver(self, name: UITextFieldTextDidChangeNotification, object: textField)
    }

    func textFieldModified() {
        if (fieldUnderEdit == nameField) {
            entryVM!.setNameWithNSString(self.nameField.text)
        } else if (fieldUnderEdit == descriptionField) {
            entryVM!.setDescriptionWithNSString(self.descriptionField.text)
        }
    }
 }

extension DetailViewController: EntrySelectionDelegate {
    func entrySelected(newEntry: EntryViewModel?) {
        entryVM?.save()
        entryVM = newEntry
    }
}

