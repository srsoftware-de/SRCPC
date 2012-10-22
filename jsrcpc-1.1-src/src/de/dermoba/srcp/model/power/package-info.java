/**
 * This package provides a model for an SRCP power supply.
 * 
 * This package is an add-on to the models provided by the original
 * version of the jsrcpc package. It tries to follow the design principles
 * as they could be derived from the code. However, the interpretation
 * of the intentions of the design may not be accurate.
 * 
 * There is a deliberate difference in the use of the device class.
 * In the other model packages, each modeled item has an attached
 * device object. This is error prone because the device's state
 * has to be maintained. In this package a device instance is created on
 * the fly whenever necessary which keeps things simpler.
 */
package de.dermoba.srcp.model.power;
