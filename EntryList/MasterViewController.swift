//
//  MasterViewController.swift
//  MathMonsters
//
//  Created by Stefan Buehlmann on 12.05.16.
//  Copyright © 2016 Stefan Buehlmann. All rights reserved.
//
//  see tutorial at
//  https://www.raywenderlich.com/94443/uisplitviewcontroller-tutorial-getting-started

import UIKit

protocol EntrySelectionDelegate: class {
    func entrySelected(newEntry: EntryViewModel?)
}

class MasterViewController: UITableViewController, JavaBeansPropertyChangeListener, UISplitViewControllerDelegate {

    var entryListVM: EntryListViewModel?
    var currentlySelectedEntryVM: EntryViewModel?
    weak var delegateDetailViewC: EntrySelectionDelegate?

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)!
    }
   
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let dbEntryService = DbEntryService()
        entryListVM = EntryListViewModel(entryServiceI: dbEntryService)
        entryListVM!.addPropertyChangeListenerWithJavaBeansPropertyChangeListener(self)
        self.changeSelection(nil)

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.leftBarButtonItem = self.editButtonItem()
        // self.navigationItem.leftBarButtonItem = self.addButtonItem()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // return the number of sections
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // return the number of rows
        return Int(self.entryListVM!.count())
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("Cell", forIndexPath: indexPath)
        // Configure the cell..., remark, this is ANY of the N entries shown, its not about selecting one
        let entryVM = self.entryListVM!.findWithInt(Int32(indexPath.row))
        cell.textLabel?.text = "\(entryVM.getId()) / \(entryVM.getName()) / \(entryVM.getDescription())"
        return cell
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        self.changeSelection(self.entryListVM!.findWithInt(Int32(indexPath.row)))
        if let detailViewController = self.delegateDetailViewC as? DetailViewController {
            splitViewController?.showDetailViewController(detailViewController.navigationController!, sender: nil)
        }
    }

    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */
    
    // support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            // Delete the row from the data source
            self.entryListVM!.delete__WithInt(Int32(indexPath.row))
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
            self.changeSelection(nil)
        }
    }
    
    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    // support reordering the table view.
    override func tableView(tableView: UITableView, moveRowAtIndexPath fromIndexPath: NSIndexPath, toIndexPath: NSIndexPath) {
        self.entryListVM!.moveWithInt(Int32(fromIndexPath.row), withInt: Int32(toIndexPath.row))
    }

    /*
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    
    @IBAction func addNewItem(sender: AnyObject) {
        // Create a new Item and add it to the store
        let entry = self.entryListVM!.create()
        // Figure out where that item is in the array
        let index = self.entryListVM!.indexOfWithEntryViewModel(entry)
        if index >= 0 {
            let indexPath = NSIndexPath(forRow: Int(index), inSection: 0)
            tableView.insertRowsAtIndexPaths([indexPath], withRowAnimation: .Automatic)
        }
    }
    
    @IBAction func toggleEditingMode(sender: AnyObject) {
        if editing {
            sender.setTitle("Edit", forState: .Normal)
            setEditing(false, animated: true)
        } else {
            sender.setTitle("Done", forState: .Normal)
            setEditing(true, animated: true)
        }
    }
    
    private func changeSelection(newSelection: EntryViewModel?) {
        if currentlySelectedEntryVM == newSelection {
            return;
        }
        // register at newSelection
        currentlySelectedEntryVM = newSelection
        // and notify delegateDetailViewC
        self.delegateDetailViewC?.entrySelected(currentlySelectedEntryVM)
    }
    
    func propertyChangeWithJavaBeansPropertyChangeEvent(event: JavaBeansPropertyChangeEvent!) {
        switch event.getPropertyName() {
        case EntryListViewModel_ENTRY_AT_CHG:
            if let newValue: AnyObject = event.getNewValue() {
                let i = newValue as! NSInteger as Int
                let indexPath = NSIndexPath(forRow: Int(i), inSection: 0)
                tableView.reloadRowsAtIndexPaths([indexPath], withRowAnimation: .Automatic)
            } else {
                print("nothing changed?")
            }
        default:
            print("all changed? name=\(event.getPropertyName())")
            tableView.reloadData()
        }
    }
    
    /************** UISplitViewControllerDelegate **************/
    func splitViewController(splitViewController: UISplitViewController,
           collapseSecondaryViewController secondaryViewController: UIViewController,
           ontoPrimaryViewController primaryViewController: UIViewController) -> Bool {
        // this method makes sure that the MasterViewController is shown first (and not the Detail) 
        // TODO: this does not yet work fully correcly when started on iPad portrait
        return true;
    }

}

